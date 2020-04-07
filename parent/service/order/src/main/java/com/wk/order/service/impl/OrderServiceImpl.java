package com.wk.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.goods.entity.Sku;
import com.wk.goods.feign.SKUFeign;
import com.wk.order.entity.Order;
import com.wk.order.entity.OrderItem;
import com.wk.order.mapper.OrderItemMapper;
import com.wk.order.mapper.OrderMapper;
import com.wk.order.mq.queue.QueueConfig;
import com.wk.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wk.pay.feign.PayFeign;
import com.wk.user.feign.UserFeign;
import constant.RedisKeyConstant;
import entity.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SKUFeign skuFeign;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private PayFeign payFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 支付失败，关闭支付、删除订单(修改订单状态)、回滚库存
     * @param outTradeNo 订单号
     */
    @Override
    public void deleteOrder(String outTradeNo) {
        //关闭微信支付
        payFeign.close(outTradeNo);

        //修改订单状态
        Order order = new Order();
        order.setId(outTradeNo);
        order.setPayStatus("2");    //支付失败
        order.setUpdateTime(new Date());
        order.setIsDelete("1");     //删除订单
        orderMapper.updateById(order);

        //回滚库存
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(outTradeNo);

        //根据订单ID查询该订单下的所有商品
        List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<>(orderItem));

        Map<String,Object> paramMap = new HashMap<>();
        for (OrderItem item : orderItems) {
            //获取订单中的商品ID和对应商品个数
            paramMap.put(item.getSkuId().toString(),item.getNum());
        }

        //删除对应订单中的所有商品
        orderItemMapper.delete(new QueryWrapper<>(orderItem));

        //调用goods微服务回滚库存
        skuFeign.rollbackCount(paramMap);
    }

    /**
     * 支付完成，修改订单信息
     * @param outTradeNo    订单号
     * @param payTime       支付完成时间
     * @param transactionId 交易流水号
     */
    @Override
    public void updateStatus(String outTradeNo, String payTime, String transactionId) {
        try {
            //根据订单ID查询订单
            Order order = orderMapper.selectById(outTradeNo);
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(payTime);
            order.setPayTime(date);     //支付完成时间
            order.setPayStatus("1");    //支付状态，已完成
            order.setTransactionId(transactionId);  //交易流水号
            orderMapper.updateById(order);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 购物车结算生成订单
     * @param order
     */
    @GlobalTransactional        //分布式事务
    @Override
    public void add(Order order) {

        order.setId(String.valueOf(idWorker.nextId()));     //订单ID

        /**
         * 订单购买商品总数量=订单中每个商品的总数量之和
         * 订单总金额=订单中每个商品的金额之和
         * 从购物车集合中获取被选中商品的明细
         * 循环商品明细，每个商品的购买数量和金额叠加
         */
        //存放购物车被选中的商品
        List<OrderItem> orderItems = new ArrayList<>();

        for (Long skuId : order.getSkuIds()) {
            orderItems.add((OrderItem) redisTemplate.boundHashOps(RedisKeyConstant.Cart + order.getUsername()).get(skuId));
            //从购物车中删除被选中的商品
            redisTemplate.boundHashOps(RedisKeyConstant.Cart + order.getUsername()).delete(skuId);
        }

        int totalNum = 0;       //订单购买商品总数量
        int totalMoney = 0;     //订单总金额

        //封装商品库存递减需要的参数
        Map<String,Integer> decrMap = new HashMap<>();

        for (OrderItem orderItem : orderItems) {
            totalNum+=orderItem.getNum();

            //totalMoney+=orderItem.getMoney();
            //下单时查询数据库中的商品价格，避免购物车中商品价格被修改
            Sku sku = skuFeign.findById(orderItem.getSkuId()).getData();
            totalMoney+=(orderItem.getNum()*sku.getPrice());

            //订单明细的ID
            orderItem.setId(String.valueOf(idWorker.nextId()));

            //订单明细所属的订单
            orderItem.setOrderId(order.getId());

            //是否退货
            orderItem.setIsReturn("0");

            //订单商品明细添加多次
            orderItemMapper.insert(orderItem);

            decrMap.put(orderItem.getSkuId().toString(),orderItem.getNum());
        }

        //订单添加1次
        order.setTotalNum(totalNum);            //订单购买商品总数量
        order.setTotalMoney(totalMoney);        //订单总金额
        order.setPayMoney(totalMoney);          //实付金额
        Date date = new Date();
        order.setCreateTime(date);              //订单创建时间
        order.setUpdateTime(date);              //订单修改时间
        order.setSourceType("1");               //订单来源，1：web页面
        order.setOrderStatus("0");              //订单状态，0：未支付
        order.setPayStatus("0");                //支付状态，0：未支付
        order.setIsDelete("0");                 //未删除

        //添加订单
        orderMapper.insert(order);

        //减少库存
        skuFeign.decrCount(decrMap);

        //用户下单后添加积分
        userFeign.addPoint(1);

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("订单创建时间 = " + now);

        //生成订单后发送延时消息给MQ，用于处理超时未支付的订单
        //1、路由的名字；2、要发送的消息(订单号)；3、消息处理对象
        rabbitTemplate.convertAndSend(QueueConfig.ORDER_DELAY_QUEUE, order.getId(), message -> {
            //设置延时读取
            message.getMessageProperties().setExpiration("60000");
            return message;
        });
    }
}

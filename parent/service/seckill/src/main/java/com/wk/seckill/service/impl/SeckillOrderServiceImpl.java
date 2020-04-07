package com.wk.seckill.service.impl;

import com.wk.pay.feign.PayFeign;
import com.wk.seckill.entity.SeckillGoods;
import com.wk.seckill.entity.SeckillOrder;
import com.wk.seckill.mapper.SeckillGoodsMapper;
import com.wk.seckill.mapper.SeckillOrderMapper;
import com.wk.seckill.service.SeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wk.seckill.task.MultiThreadingCreateOrder;
import constant.RedisKeyConstant;
import entity.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private MultiThreadingCreateOrder createOrder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private PayFeign payFeign;

    @Override
    public void deleteOrder(String username, String outTradeNo) {
        payFeign.close(outTradeNo);     //关闭支付

        //删除用户秒杀订单排队信息
        redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrder).delete(username);

        //订单状态
        SeckillStatus status = (SeckillStatus)redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).get(username);

        //清除用户排队信息
        clearQueue(username);

        //回滚库存，Redis中对应商品库存递增，Redis中的对应商品可能已经卖完被删除
        //查询秒杀商品
        String namespace = RedisKeyConstant.SeckillGoods + status.getTime();
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(namespace).get(status.getGoodsId());

        /**
         * 如果Redis中的商品已售罄，从数据库中查询对应秒杀商品数据，
         * 此时秒杀商品库存是0，只有库存为0才会删除Redis中的商品信息
         */
        if (seckillGoods == null) {
            seckillGoods = seckillGoodsMapper.selectById(status.getGoodsId());
            //秒杀设定一个用户只能抢购一个商品，数据库库存为0，回滚直接设置为1
            seckillGoods.setStockCount(1);

            //回滚后的库存同步到MySQL，库存>0，可以存到Redis中继续秒杀
            seckillGoodsMapper.updateById(seckillGoods);
        }else {
            //商品没卖完，只回滚库存数量，不需要同步到MySQL
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
        }
        //回滚后的秒杀商品信息同步到Redis中
        redisTemplate.boundHashOps(namespace).put(seckillGoods.getId(),seckillGoods);

        //库存队列回滚，库存+1
        redisTemplate.boundHashOps(RedisKeyConstant.SeckillGoodsCountList).increment(seckillGoods.getId(),1);
    }

    /**
     * 支付成功，更新秒杀订单状态
     * @param username      用户名
     * @param transactionId 交易流水号
     * @param endTime       交易时间
     */
    @Override
    public void updatePayStatus(String username, String transactionId, String endTime) {
        //根据用户名从Redis中获取秒杀订单信息
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrder).get(username);

        if (seckillOrder != null) {
            try {
                seckillOrder.setStatus("1");        //修改订单状态为已支付
                seckillOrder.setTransactionId(transactionId);   //交易流水号
                //支付时间
                seckillOrder.setPayTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime));

                //秒杀订单支付成功后保存到数据库
                seckillOrderMapper.insert(seckillOrder);

                //删除用户秒杀订单排队信息
                redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrder).delete(username);

                //删除用户排队信息
                clearQueue(username);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 参加秒杀的用户排队创建秒杀订单
     * @param time
     * @param goodsId
     * @param username
     */
    @Override
    public void addOrder(String time, Long goodsId, String username) {

        /**
         * 创建订单时增加用户排队的次数；次数大于1表示用户重复抢单
         * key：username，value：自增的值；返回值：自增操作之后的值
         * redis是单线程，每次只能有一个线程操作Redis，返回的值永远不会重复
         */
        Long increment = redisTemplate.boundHashOps(RedisKeyConstant.UserQueueCount).increment(username, 1);
        if (increment > 1) {
            throw new RuntimeException("重复抢单");
        }

        //用户排队状态对象
        SeckillStatus seckillStatus = new SeckillStatus(username,new Date(),1,goodsId,time);

        //用户排队抢单信息放入Redis的队列中；左存右取
        redisTemplate.boundListOps(RedisKeyConstant.UserQueueStatus).leftPush(seckillStatus);

        //秒杀商品订单状态信息放入Redis的Mpa中，用于获取秒杀订单状态信息
        redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).put(username,seckillStatus);

        createOrder.createOrder();
    }

    @Override
    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).get(username);
    }

    /**
     * 清理用户排队抢单信息
     */
    private void clearQueue(String username) {
        //用户排队的次数
        redisTemplate.boundHashOps(RedisKeyConstant.UserQueueCount).delete(username);

        //秒杀商品订单状态信息
        redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).delete(username);
    }
}

package com.wk.seckill.task;

import com.alibaba.fastjson.JSON;
import com.wk.seckill.entity.SeckillGoods;
import com.wk.seckill.entity.SeckillOrder;
import com.wk.seckill.mapper.SeckillGoodsMapper;
import com.wk.seckill.mq.CreateDelayQueue;
import constant.RedisKeyConstant;
import entity.IdWorker;
import entity.SeckillStatus;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper goodsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @Async 该方法会异步执行，底层由多线程实现
     */
    @Async
    public void createOrder(){

        try {
            System.out.println("准备下单...");
            Thread.sleep(10000);

            //从Redis队列中获取用户排队信息
            SeckillStatus status = (SeckillStatus) redisTemplate.boundListOps(RedisKeyConstant.UserQueueStatus).rightPop();

            //队列中没有用户排队信息就返回
            if (status == null) {
                return;
            }

            String time = status.getTime();
            Long goodsId = status.getGoodsId();
            String username = status.getUsername();

            //下单之前先递减商品库存队列中对应商品的库存
            Long skGoodsCount = redisTemplate.boundHashOps(RedisKeyConstant.SeckillGoodsCountList).increment(goodsId, -1);
            System.out.println("秒杀商品剩余库存 = " + skGoodsCount);
            if(skGoodsCount<0){
                //商品已售罄，需要清理所有排队相关信息
                clearQueue(username);
                return;
            }

            //查询秒杀商品
            String namespace = RedisKeyConstant.SeckillGoods + time;
            SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps(namespace).get(goodsId);

            //判断商品库存是否>0
            if (goods == null || goods.getStockCount()<=0) {
                throw new RuntimeException("已售罄");
            }

            //创建订单对象
            SeckillOrder order = new SeckillOrder();
            order.setId(idWorker.nextId());     //订单ID
            order.setSeckillId(goodsId);                //秒杀商品ID 一个用户只能抢购一个秒杀商品
            order.setMoney(goods.getCostPrice());    //支付金额
            order.setUserId(username);          //用户名
            order.setCreateTime(new Date());    //订单创建时间
            order.setStatus("0");               //未支付

            /**
             * 为了提高效率，将订单对象存储到Redis中
             *  一个用户只能有一个未支付订单
             *  使用hash存储秒杀订单信息，命名空间是seckillOrder key:username value:seckillOrder
             */
            redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrder).put(username,order);

            /**
             * 商品库存递减
             * 递减后如果商品库存为0，删除Redis中对应的商品信息，并将Redis中对应的数据同步到MySQL
             * 递减后商品库存不为0，则更新Redis中的商品库存
             */
            //goods.setStockCount(goods.getStockCount()-1);
            goods.setStockCount(skGoodsCount.intValue());

            Thread.sleep(10000);
            System.out.println("操作后剩余库存 = " + skGoodsCount);

            //判断Redis递减操作后的商品库存
            if(skGoodsCount<=0){
                //同步数据到MySQL
                goodsMapper.updateById(goods);

                redisTemplate.boundHashOps(namespace).delete(goodsId);
            }else {
                redisTemplate.boundHashOps(namespace).put(goodsId,goods);
            }

            //下单成功 更新订单状态
            status.setOrderId(order.getId());   //订单ID
            status.setMoney(Float.valueOf(goods.getCostPrice().toString()));   //支付金额
            status.setStatus(2);        //待付款
            redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).put(username,status);

            //秒杀订单创建时间
            System.out.println("秒杀订单创建时间："+new SimpleDateFormat("HH:mm:ss").format(new Date()));

            //发送消息给延时队列，用于处理超过30分钟完成支付的订单
            rabbitTemplate.convertAndSend(
                    CreateDelayQueue.SECKILL_DELAY_QUEUE     //发送给延时队列
                    , (Object) JSON.toJSONString(status)     //要发送的数据
                    , new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //设置消息延时时间，单位/毫秒
                            message.getMessageProperties().setExpiration("30000");
                            return message;
                        }
                    }
            );

            System.out.println("下单完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

package com.wk.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.wk.pay.feign.PayFeign;
import com.wk.seckill.service.SeckillOrderService;
import constant.RedisKeyConstant;
import entity.SeckillStatus;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 监听超时秒杀订单，如果监听到就关闭支付、取消订单、回滚库存
 */
@Component
@RabbitListener(queues = CreateDelayQueue.SECKILL_QUEUE)     //监听Queue2队列
public class DelaySeckillMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 监听MultiThreadingCreateOrder.createOrder()发送的延时消息
     * 如果监听到就关闭支付、取消订单、回滚库存
     * @param message
     */
    @RabbitHandler
    public void getMessage(String message){

        System.out.println("秒杀订单回滚时间："+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        System.out.println("监听到的超时秒杀订单信息： " + message);
        try {
            //获取用户的排队信息
            SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);

            //下单成功会清除订单信息，如果不为空，说明该订单没有完成支付
            Object obj = redisTemplate.boundHashOps(RedisKeyConstant.SeckillOrderStatus).get(seckillStatus.getUsername());
            if (obj != null) {
                //关闭支付、删除订单、回滚库存
                seckillOrderService.deleteOrder(seckillStatus.getUsername(),seckillStatus.getOrderId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.wk.pay.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 支付系统发送消息到订单系统
 * 创建队列以及交换机并让队列和交换机绑定
 */
@Configuration
public class MQConfig {

    //读取配置文件中信息的对象
    @Autowired
    private Environment env;

    /**
     * 创建队列
     */
    @Bean
    public Queue orderQueue(){
        //参数是队列的名字
        return new Queue(env.getProperty("mq.pay.queue.order"));
    }

    /**
     * 创建交换机
     */
    @Bean
    public Exchange orderExchange(){
        //1、交换机名字；2、是否持久化(重启后依然存在)；3、是否自动删除
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"),true,false);
    }

    /**
     * 队列绑定交换机
     */
    @Bean
    public Binding orderBindingExchange(Queue orderQueue, Exchange orderExchange){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)  //队列orderQueue绑定到交换机orderExchange上
                .with(env.getProperty("mq.pay.routing.key"))     //路由键
                .noargs();
    }

    /*--------------------------秒杀队列创建-------------------------------------*/

    /**
     * 创建队列
     */
    @Bean
    public Queue seckillOrderQueue(){
        //参数是队列的名字
        return new Queue(env.getProperty("mq.pay.queue.seckillOrder"));
    }

    /**
     * 创建交换机
     */
    @Bean
    public Exchange seckillOrderExchange(){
        //1、交换机名字；2、是否持久化；3、是否自动删除
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillOrder"),true,false);
    }

    /**
     * 队列绑定交换机
     */
    @Bean
    public Binding seckillOrderBindingExchange(Queue seckillOrderQueue,Exchange seckillOrderExchange){
        return BindingBuilder
                .bind(seckillOrderQueue)
                .to(seckillOrderExchange)  //队列orderQueue绑定到交换机orderExchange上
                .with(env.getProperty("mq.pay.routing.seckillKey"))     //路由键
                .noargs();
    }
}

package com.wk.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀订单的延时队列，用于处理超时未支付的秒杀订单
 * Queue1：负责数据暂时存储，30分钟后将消息发送到Queue2
 * Queue2：Queue1的消息最终发送到该队列，监听该队列
 */
@Configuration
public class CreateDelayQueue {

    /**
     * Queue1：负责数据暂时存储，30分钟后将消息发送到Queue2
     */
    public static final String SECKILL_DELAY_QUEUE = "seckill.delay.queue";

    /**
     * Queue2：Queue1的消息最终发送到该队列，监听该队列
     */
    public static final String SECKILL_QUEUE = "seckill.queue";

    /**
     * 交换机
     */
    public static final String SECKILL_EXCHANGE = "seckill.exchange";


    /**
     * 创建延时队列Queue1 秒杀订单消息发送给此队列
     * 当前队列的消息一旦过期则进入到死信队列交换机，并将死信队列中的数据路由到指定队列中
     */
    @Bean
    public Queue delayMessageQueue() {
        return QueueBuilder.durable(SECKILL_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", SECKILL_EXCHANGE)        // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key", SECKILL_QUEUE)   // 绑定指定的routing-key
                .build();
    }

    /**
     * 创建需要监听的Queue2，Queue1发送消息到该队列
     */
    @Bean
    public Queue messageQueue() {
        return new Queue(SECKILL_QUEUE, true);
    }

    /***
     * 创建交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(SECKILL_EXCHANGE);
    }


    /***
     * 队列绑定交换机
     * @param messageQueue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding basicBinding(Queue messageQueue, DirectExchange directExchange) {
        return BindingBuilder
                .bind(messageQueue)
                .to(directExchange)
                .with(SECKILL_QUEUE);
    }

}

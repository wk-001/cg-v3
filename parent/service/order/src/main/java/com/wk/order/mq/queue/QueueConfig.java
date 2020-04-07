package com.wk.order.mq.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列
 * 用户下订单后向队列1发送一个消息，30分钟后队列1的消息发送到队列2，监听队列2，间接实现延时队列
 */
@Configuration
public class QueueConfig {

    /**
     * Queue1，用于转发延时消息
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    /**
     * Queue2，用于接收延时消息
     */
    public static final String ORDER_QUEUE = "order.queue";

    /**
     * 订单延时队列交换机
     */
    public static final String ORDER_EXCHANGE = "order.exchange";



    /**
     * Queue1，用于转发延时30分钟的消息给Queue2
     * 死信队列：消息队列中的数据超出一定时间没有被读取到，被放弃读取的数据
     * Queue1的消息超时后进入死信队列，绑定到指定的死信队列交换机
     * x-dead-letter-exchange：出现dead letter之后将dead letter重新发送到指定exchange
     * 死信队列中的数据绑定到指定的routing-key
     * x-dead-letter-routing-key：出现dead letter之后将dead letter重新按照指定的routing-key发送
     */
    @Bean
    public Queue delayMessageQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)        // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key", ORDER_QUEUE)   // 绑定指定的routing-key
                .build();
    }

    /**
     * Queue2，用于接收延时消息
     * @return
     */
    @Bean
    public Queue messageQueue() {
        //参数：队列名字、是否持久化
        return new Queue(ORDER_QUEUE, true);
    }

    /***
     * 创建交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(ORDER_EXCHANGE);
    }


    /***
     *  Queue2绑定Exchange
     *  出现死信后重新发送到指定exchange和指定的routing-key，
     *  Queue2绑定Exchange，所以死信会发送到Queue2
     */
    @Bean
    public Binding basicBinding(Queue messageQueue, DirectExchange directExchange) {
        return BindingBuilder
                .bind(messageQueue)
                .to(directExchange)
                .with(ORDER_QUEUE);
    }
}

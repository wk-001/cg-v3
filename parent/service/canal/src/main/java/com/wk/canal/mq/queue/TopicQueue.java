package com.wk.canal.mq.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建队列和交换机并绑定
 * 要在rabbitmq中创建一个名为topic.queue.spu的队列
 */
@Configuration
public class TopicQueue {

    public static final String TOPIC_QUEUE_SPU = "topic.queue.spu";
    public static final String TOPIC_EXCHANGE_SPU = "topic.exchange.spu";

    /**
     * Topic模式 SPU变更队列
     * @return
     */
    @Bean
    public Queue topicQueueSpu() {
        return new Queue(TOPIC_QUEUE_SPU);
    }

    /***
     * SPU队列交换机
     * @return
     */
    @Bean
    public TopicExchange topicSpuExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_SPU);
    }

    /***
     * 队列绑定交换机
     * bind 绑定队列
     * to 到哪个交换机上
     * with 路由键 表明是哪个路由的
     * @return
     */
    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(topicQueueSpu()).to(topicSpuExchange()).with(TOPIC_QUEUE_SPU);
    }
}

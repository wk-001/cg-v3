package com.wk.canal.mq.send;

import com.alibaba.fastjson.JSON;
import entity.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送Topic消息
 */
@Component
public class TopicMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /***
     * Topic消息发送
     * @param message
     */
    public void sendMessage(Message message){
        rabbitTemplate.convertAndSend(message.getExechange(), message.getRoutekey(), JSON.toJSONString(message));
    }
}

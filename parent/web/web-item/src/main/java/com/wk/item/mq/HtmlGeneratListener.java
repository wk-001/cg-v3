package com.wk.item.mq;

import com.alibaba.fastjson.JSON;
import com.wk.item.service.PageService;
import entity.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "topic.queue.spu")     //指定要监听的队列
public class HtmlGeneratListener {

    @Autowired
    private PageService pageService;

    /**
     * 被更改的spu会被canal发送到RabbitMQ的队列，监听队列，获得被修改spu的ID，重新生成静态页面
     */
    @RabbitHandler
    public void generateHtml(String msg){
        //将数据转成Message类型
        Message message = JSON.parseObject(msg, Message.class);
        if (message.getCode()==2){      //数据被修改
            pageService.createHtml(Long.parseLong(message.getContent().toString()));
        }
    }

}

package com.wk.order.mq.listener;

import com.wk.order.entity.Order;
import com.wk.order.mq.queue.QueueConfig;
import com.wk.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 监听Queue2中的过期消息，Queue1中的消息超过指定时间会自动发送到Queue2死信队列
 */
@Component
@RabbitListener(queues = QueueConfig.ORDER_QUEUE)      //指定监听队列
public class DelayMessageListener {

    @Autowired
    private OrderService orderService;

    /**
     * 延时队列监听
     * @param message
     */
    @RabbitHandler
    public void getDelayMessage(String message){
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("监听到消息的时间 = " + format);
        System.out.println("监听到未支付的订单ID = " + message);
        //查询数据库，如果订单未支付，则关闭支付、删除订单(修改状态)、回滚库存
        Order order = orderService.getById(message);
        if(order!=null && !"1".equals(order.getPayStatus())){
            orderService.deleteOrder(message);
        }
    }

}

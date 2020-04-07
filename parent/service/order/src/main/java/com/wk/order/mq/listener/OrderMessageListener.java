package com.wk.order.mq.listener;

import com.alibaba.fastjson.JSON;
import com.wk.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听支付微服务发送到MQ的对应信息，根据支付结果修改订单状态或取消订单
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.order}")   //指定要监听的队列
public class OrderMessageListener {

    @Autowired
    private OrderService orderService;

    /**
     * 支付结果监听
     * @param message
     */
    @RabbitHandler
    public void getMessage(String message){
        //支付结果
        Map<String,String> map = JSON.parseObject(message, Map.class);
        System.out.println("监听到支付微服务发送的支付结果  = " + map);

        /**
         * 判断支付结果是否成功
         * 通信标识：return_code
         * 业务结果：result_code
         * 微信支付交易流水号：transaction_id
         * 订单号：out_trade_no
         * 支付完成时间：time_end
         */
        String outTradeNo = map.get("out_trade_no");

        //通信标识 判断方法是否执行成功
        if("SUCCESS".equals(map.get("return_code"))){

            //判断业务结果，如果支付成功，修改支付完成时间和交易流水号
            if("SUCCESS".equals(map.get("result_code"))){
                orderService.updateStatus(
                        outTradeNo,
                        map.get("time_end"),        //微信返回支付结果中获取的支付完成时间
                        map.get("transaction_id")   //微信生成的交易流水号
                );
            }else {  //支付失败，关闭支付、取消订单、回滚库存
                orderService.deleteOrder(outTradeNo);
            }
        }
    }

}

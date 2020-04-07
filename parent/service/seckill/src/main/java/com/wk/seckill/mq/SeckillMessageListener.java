package com.wk.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.wk.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听秒杀订单支付状态
 * 支付成功则修改订单状态、清理用户排队信息
 * 支付失败则删除订单、回滚库存
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillOrder}")     //指定要监听的队列
public class SeckillMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监听WeixinPayController.notifyUrl()方法微信支付回调的信息
     * 根据监听到的秒杀订单信息后修改秒杀订单状态
     * @param message
     */
    @RabbitHandler
    public void getMessage(String message){
        System.out.println("监听到的秒杀订单信息： " + message);
        try {
            //将json类型的支付信息转成map
            Map<String,String> map = JSON.parseObject(message,Map.class);
            /**
             * return_code：通信标识
             * result_code：业务结果；
             *  success：修改订单状态，
             *  fail：关闭支付、删除订单(真实工作存入到MySQL)、回滚库存
             * out_trade_no：订单号
             */
            String outTradeNo = map.get("out_trade_no");
            Map<String,String> attach = JSON.parseObject(map.get("attach"), Map.class);//自定义数据
            String username = attach.get("username");
            if("SUCCESS".equals(map.get("return_code"))){       //通信结果
                if("SUCCESS".equals(map.get("result_code"))){   //业务结果
                    //修改订单状态、清理用户排队信息
                    seckillOrderService.updatePayStatus(
                            username
                            ,map.get("transaction_id")
                            ,map.get("time_end")
                    );
                }else {
                    //删除订单、回滚库存
                    seckillOrderService.deleteOrder(username,outTradeNo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

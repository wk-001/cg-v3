package com.wk.order.service;

import com.wk.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Order> {

    /**
     * 支付失败，关闭支付、删除订单(修改订单状态)、回滚库存
     * @param outTradeNo 订单号
     */
    void deleteOrder(String outTradeNo);

    /**
     * 支付成功，修改订单状态、支付时间
     * @param outTradeNo    订单号
     * @param payTime       支付完成时间
     * @param transactionId 交易流水号
     */
    void updateStatus(String outTradeNo,String payTime,String transactionId);

    /**
     * 购物车结算生成订单
     * @param order
     */
    void add(Order order);
}

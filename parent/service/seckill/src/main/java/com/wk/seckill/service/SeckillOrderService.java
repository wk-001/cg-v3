package com.wk.seckill.service;

import com.wk.seckill.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import entity.SeckillStatus;

public interface SeckillOrderService extends IService<SeckillOrder> {

    /**
     * 关闭支付，删除秒杀订单，回滚库存
     * @param username
     */
    void deleteOrder(String username,String outTradeNo);

    /***
     * 支付成功，更新秒杀订单状态
     * @param username      用户名
     * @param transactionId 交易流水号
     * @param endTime       交易时间
     */
    void updatePayStatus(String username, String transactionId, String endTime);

    /**
     * 创建秒杀订单
     * @param time
     * @param goodsId
     * @param username
     */
    void addOrder(String time, Long goodsId, String username);

    /**
     * 秒杀商品订单状态查询
     * @param username
     * @return
     */
    SeckillStatus queryStatus(String username);
}

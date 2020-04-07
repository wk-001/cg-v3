package com.wk.seckill.controller;

import com.wk.seckill.service.SeckillOrderService;
import entity.Result;
import entity.SeckillStatus;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀商品订单状态查询
     * @param username
     * @return
     */
    @GetMapping("query")
    public Result<SeckillStatus> queryStatus(String username){
        username = "szitheima";
        SeckillStatus seckillStatus = seckillOrderService.queryStatus(username);
        if (seckillStatus != null) {
            return new Result<>(seckillStatus);
        }
        return new Result<>(false,StatusCode.NOTFOUNDERROR,"没有抢单信息");
    }

    /**
     * 添加秒杀订单
     * @param time 时间区间
     * @param goodsId 商品ID
     * @return
     */
    @RequestMapping("add")
    public Result addOrder(String time, Long goodsId, String username){
        seckillOrderService.addOrder(time,goodsId,username);
        return new Result("正在排队...");
    }
}


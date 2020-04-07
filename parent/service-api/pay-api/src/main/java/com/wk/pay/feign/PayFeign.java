package com.wk.pay.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("pay")
@RequestMapping("/weixin/pay")
public interface PayFeign {

    /***
     * 关闭支付
     * @param outTradeNo 订单号
     * @return
     */
    @GetMapping(value = "/close")
    Result close(@RequestParam String outTradeNo);

}

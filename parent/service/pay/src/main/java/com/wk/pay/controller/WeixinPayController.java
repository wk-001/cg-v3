package com.wk.pay.controller;

import com.wk.pay.service.WeixinPayService;
import constant.WeixinPayUrl;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("weixin/pay")
@CrossOrigin
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    /***
     * 关闭订单的微信支付
     * @param outTradeNo 订单号
     * @return
     */
    @GetMapping(value = "/close")
    public Result close(String outTradeNo){
        return new Result("订单关闭成功！",weixinPayService.queryOrClose(outTradeNo,WeixinPayUrl.closeUrl));
    }

    /**
     * 支付回调方法，从微信返回的网络输入流xml数据中获取支付结果
     * @param request
     * @return
     */
    @RequestMapping(value = "/notify/url")
    public String notifyUrl(HttpServletRequest request){
        weixinPayService.notifyUrl(request);
        String result = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        return result;
    }

    /***
     * 查询订单支付状态
     * @param outTradeNo
     * @return
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String outTradeNo){
        return new Result("订单查询成功",weixinPayService.queryOrClose(outTradeNo, WeixinPayUrl.queryUrl));
    }

    /**
     * 创建支付二维码
     *  普通订单：
     *      exchange：exchange.order
     *      routingKey：queue.order
     *   秒杀订单：
     *      exchange：exchange.seckillOrder
     *      routingKey：queue.seckillOrder
     *
     * @param paramMap
     * @return
     */
    @GetMapping("create/native")
    public Result createNative(@RequestParam Map<String,String> paramMap){
        return new Result("二维码预付订单创建成功！",weixinPayService.createNative(paramMap));
    }

}

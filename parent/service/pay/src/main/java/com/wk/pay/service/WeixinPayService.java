package com.wk.pay.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface WeixinPayService {

    /**
     * 创建支付二维码
     * @param paramMap
     * @return
     */
    Map<String, String> createNative(Map<String,String> paramMap);

    /**
     * 查询订单/关闭支付
     * @param outTradeNo 商户订单号
     * @param url 指定URL是查询订单或关闭支付
     * @return
     */
    Map<String, String> queryOrClose(String outTradeNo, String url);

    /**
     * 从微信返回的网络输入流数据中获取支付结果
     * @param request
     */
    void notifyUrl(HttpServletRequest request);

}

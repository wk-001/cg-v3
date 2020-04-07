package com.wk.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import com.wk.pay.service.WeixinPayService;
import constant.WeixinPayUrl;
import entity.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    //应用ID
    @Value("${weixin.appid}")
    private String appid;

    //商户ID
    @Value("${weixin.partner}")
    private String partner;

    //秘钥
    @Value("${weixin.partnerkey}")
    private String partnerkey;

    //支付回调地址
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void notifyUrl(HttpServletRequest request) {
        try {
            //获取网络输入流
            ServletInputStream is = request.getInputStream();

            //定义缓冲区，将数据读取到缓冲区中
            byte[] buffer = new byte[1024];

            //缓冲区的数据写出到输出流
            ByteArrayOutputStream baos =new ByteArrayOutputStream();

            //从网络输入流中取出支付回调数据
            int len;
            //输入流将数据写入到缓冲区，缓冲区的数据再写出到输出流
            while((len=is.read(buffer))!=-1){     //!=-1表示有数据
                baos.write(buffer,0,len);
            }

            //微信支付结果的字节数组
            byte[] bytes = baos.toByteArray();

            //字节数组转xml字符串
            String xmlResult= new String(bytes, StandardCharsets.UTF_8);
            System.out.println("xmlResult = " + xmlResult);

            //xml字符串转map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("resultMap = " + resultMap);

            //获取自定义参数转成map类型；自定义参数中存放的是需要发送消息的队列名字和用户名
            Map<String,String> attach = JSON.parseObject(resultMap.get("attach"), Map.class);

            //发送支付结果给MQ
            rabbitTemplate.convertAndSend(attach.get("exchange"),attach.get("routingKey"), JSON.toJSONString(resultMap));
            //rabbitTemplate.convertAndSend("exchange.order","queue.order", JSON.toJSONString(resultMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> queryOrClose(String outTradeNo, String url) {
        try {
            //参数
            Map<String,String> param = new HashMap<>();
            param.put("appid",appid);           //应用ID
            param.put("mch_id",partner);       //商户ID
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
            param.put("out_trade_no",outTradeNo);   //商户订单号

            //map转成xml字符串可以携带签名
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            return httpClientUtil(url, signedXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建二维码
     * 普通订单：
     *     exchange：exchange.order
     *     routingKey：queue.order
     *  秒杀订单：
     *     exchange：exchange.seckillOrder
     *     routingKey：queue.seckillOrder
     * @param paramMap
     * @return
     */
    @Override
    public Map<String, String> createNative(Map<String, String> paramMap) {

        try {
            //参数
            Map<String,String> param = new HashMap<>();
            param.put("appid",appid);           //应用ID
            param.put("mch_id",partner);       //商户ID
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
            param.put("body","畅购商城商品");                    //商品描述
            param.put("out_trade_no",paramMap.get("outTradeNo"));   //商户订单号
            param.put("total_fee",paramMap.get("totalFee"));          //交易金额，单位/分
            param.put("spbill_create_ip","127.0.0.1");          //终端IP
            param.put("notify_url",notifyurl);          //交易结果回调通知地址
            param.put("trade_type","NATIVE");          //交易类型

            /**
             * 获取自定义数据，设置到附加数据attach并发送到微信服务器
             */
            Map<String,String> attachMap = new HashMap<>();
            attachMap.put("exchange",paramMap.get("exchange"));     //交换机
            attachMap.put("routingKey",paramMap.get("routingKey")); //路由

            //传入username，根据username查询Redis中的秒杀订单
            String username = paramMap.get("username");
            if(StringUtils.isNotEmpty(username)){
                attachMap.put("username",username);
            }

            param.put("attach", JSON.toJSONString(attachMap));  //自定义的附加数据

            //map转成xml字符串可以携带签名
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            return httpClientUtil(WeixinPayUrl.payUrl, signedXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Map<String, String> httpClientUtil(String url, String signedXml) throws Exception {

        HttpClient httpClient = new HttpClient(url);

        //提交方式设置为https
        httpClient.setHttps(true);

        //提交Xml参数
        httpClient.setXmlParam(signedXml);

        //执行请求
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();

        //返回数据转成map
        return WXPayUtil.xmlToMap(content);
    }
}

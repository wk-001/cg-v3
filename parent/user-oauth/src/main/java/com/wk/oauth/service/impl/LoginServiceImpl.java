package com.wk.oauth.service.impl;

import com.wk.oauth.service.LoginService;
import com.wk.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.wk.oauth.service.impl *
 * @since 1.0
 */
@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 密码授权模式需要在请求头传入客户端ID和客户端秘钥，并以参数的方式传递授权模式
     * 此方法的作用是预先定义客户端ID、客户端秘钥、授权模式。让用户只输入账号密码即可实现登录，生成令牌
     *参数传递
     * 	1、账号		username=szitheima
     * 	2、密码		password=szitheima
     * 	3、授权方式		grant_type=password
     * 请求头传递
     * 	4、Basic Base64(客户端ID:客户端秘钥)		Authorization=Basic xxxxxx
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grandType) {

        //1.定义url (申请令牌的url)
        //参数 : 微服务的名称spring.appplication指定的名称
        ServiceInstance choose = loadBalancerClient.choose("user-auth");
        String url =choose.getUri().toString()+"/oauth/token";

        //封装请求参数
        //2.定义头信息 Basic Base64加密(客户端ID:客户端秘钥)
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        String Authorization = "Basic "+Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.add("Authorization",Authorization);

        //3. 定义请求体  有授权模式 用户的名称 和密码
        MultiValueMap<String,String> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("grant_type",grandType);
        bodyData.add("username",username);
        bodyData.add("password",password);

        //需要提交的数据封装，包含请求头和请求体数据
        HttpEntity<MultiValueMap> requestentity = new HttpEntity<>(bodyData,headers);

        /**
         * 4.模拟浏览器 发送POST 请求 携带 头 和请求体 到认证服务器
         * 参数1  请求地址
         * 参数2  提交方式
         * 参数3  requestentity：请求提交的数据信息封装，请求体和请求头
         * 参数4  返回数据需要转换的类型
         */
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestentity, Map.class);

        //5.接收到返回的响应，就是用户登录后的令牌信息
        Map<String,String> token = responseEntity.getBody();

        //响应的令牌信息封装为对象
        AuthToken authToken = new AuthToken();

        //访问令牌(jwt)
        authToken.setAccessToken(token.get("access_token"));
        //刷新令牌(jwt)
        authToken.setRefreshToken(token.get("refresh_token"));
        //jti，作为用户的身份标识
        authToken.setJti(token.get("jti"));

        //6.返回
        return authToken;
    }


    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode(new String("Y2hhbmdnb3UxOmNoYW5nZ291Mg==").getBytes());
        System.out.println(new String(decode));
    }
}

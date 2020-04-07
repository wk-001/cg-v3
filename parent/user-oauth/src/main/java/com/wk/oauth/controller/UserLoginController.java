package com.wk.oauth.controller;

import com.wk.oauth.service.LoginService;
import com.wk.oauth.util.AuthToken;
import com.wk.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 接收用户的账号密码
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {

    @Autowired
    private LoginService loginService;

    //客户端id
    @Value("${auth.clientId}")
    private String clientId;

    //客户端秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    //授权模式 密码模式
    private static final String GRAND_TYPE = "password";

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;


    /**
     * 密码模式  登录认证.
     * 除了用户名和密码，还需要指定授权方式以及通过请求头传递客户端ID和客户端秘钥
     *
     *参数传递
     * 	1、账号		username=szitheima
     * 	2、密码		password=szitheima
     * 	3、授权方式		grant_type=password
     * 请求头传递
     * 	4、Basic Base64(客户端ID:客户端秘钥)		Authorization=Basic xxxxxx
     */
    @RequestMapping("/login")
    public Result<Map> login(String username, String password) {
        //登录 之后生成令牌的数据返回
        AuthToken authToken = loginService.login(username, password, clientId, clientSecret, GRAND_TYPE);

        if (authToken != null) {
            //设置到cookie中
            saveCookie(authToken.getAccessToken());
            return new Result<>("令牌生成成功",authToken);
        }
        return new Result<>(false,StatusCode.LOGINERROR,"登录失败");
    }

    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}

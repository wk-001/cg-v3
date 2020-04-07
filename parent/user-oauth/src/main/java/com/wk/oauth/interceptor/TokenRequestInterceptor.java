package com.wk.oauth.interceptor;

import com.wk.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * 在feign调用之前拦截，生成令牌并封装到头文件中，在访问各个微服务时携带令牌
 */
@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //生成admin令牌
        String token = AdminToken.adminToken("admin", "oauth");
        //将令牌封装到头文件中
        requestTemplate.header("Authorization","bearer "+token);
    }
}

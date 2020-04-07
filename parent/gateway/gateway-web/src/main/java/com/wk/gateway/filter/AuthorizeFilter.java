package com.wk.gateway.filter;

import com.wk.gateway.utils.JWTUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * 全局过滤器，实现用户权限鉴别（校验）
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //令牌名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /**
     * 全局拦截，获取、校验令牌
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //用户如果是登录或者一些不需要做权限认证的请求，直接放行
        String uri = request.getURI().toString();
        if(URLFilter.hasAuthorize(uri)){
            return chain.filter(exchange);
        }

        //获取用户令牌信息，令牌可能存在三个地方：
        // 1、头文件；获取头文件中第一个对应的参数
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);

        //true：令牌在头文件中；false：令牌不在头文件中，需要将令牌封装到头文件中，再传递给其他微服务
        boolean hasToken = true;

        // 2、参数；从所有请求参数中获取第一个对应的参数
        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            hasToken = false;
        }

        if (StringUtils.isEmpty(token)) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        //如果没有令牌则拦截
        if (StringUtils.isEmpty(token)) {
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }else {
            //判断令牌是否为空，如果非空，将令牌放入头文件中放行
            if(!hasToken) {
                //判断token是否有bearer前缀，如果没有则添加
                if(!token.startsWith("bearer ")&&!token.startsWith("Bearer ")){
                    token = "bearer "+token;
                }
                //如果请求头中没有令牌，则将令牌封装到头文件中
                request.mutate().header(AUTHORIZE_TOKEN, token);
            }
        }

        //如果有令牌，则校验令牌是否有效（升级为证书校验）
        /*try {
            JWTUtil.parseJWT(token);
        } catch (Exception e) {
            //令牌无效拦截
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }  */

        //令牌有效放行
        return chain.filter(exchange);
    }

    /**
     * 排序，越小越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

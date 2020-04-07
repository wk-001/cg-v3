package com.wk;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})     //排除自动加载数据库的类
@EnableEurekaClient
@EnableCanalClient          //开启canal客户端
@EnableFeignClients("com.wk.user.feign")     //开启feign
@EnableRabbit           //开启rabbitmq
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}
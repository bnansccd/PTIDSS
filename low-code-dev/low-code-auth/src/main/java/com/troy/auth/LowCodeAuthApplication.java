package com.troy.auth;

import com.troy.common.security.annotation.EnableCustomConfig;
import com.troy.common.security.annotation.EnableRyFeignClients;
import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/2 17:17:25
 * @Description: 认证授权中心
 * @Version: 1.0.0
 */
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.troy"})
public class LowCodeAuthApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeAuthApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeAuthApplication.class, args);
        LOGGER.info("认证授权中心启动成功");
    }
}

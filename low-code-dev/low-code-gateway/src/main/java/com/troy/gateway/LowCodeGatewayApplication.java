package com.troy.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 17:17:26
 * @Description: LowCodeGatewayApplication
 * @Version: 1.0.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = "com.troy")
@EnableFeignClients
public class LowCodeGatewayApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeGatewayApplication.class, args);
        LOGGER.info("网关启动成功");
    }
}

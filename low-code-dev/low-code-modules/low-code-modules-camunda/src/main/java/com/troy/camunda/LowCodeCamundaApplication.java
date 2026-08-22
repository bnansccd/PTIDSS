package com.troy.camunda;

import com.troy.common.security.annotation.EnableCustomConfig;
import com.troy.common.security.annotation.EnableRyFeignClients;
import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 15:15:39
 * @Description: 工作流
 * @Version: 1.0.0
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class LowCodeCamundaApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeCamundaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeCamundaApplication.class, args);
        LOGGER.info("工作流模块启动成功");
    }
}

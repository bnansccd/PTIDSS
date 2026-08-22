package com.troy.system;

import com.troy.common.security.annotation.EnableCustomConfig;
import com.troy.common.security.annotation.EnableRyFeignClients;
import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/3 15:15:28
 * @Description: 系统模块
 * @Version: 1.0.0
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
@MapperScan("com.troy.**.mapper")
@ComponentScan(basePackages = "com.troy")
public class LowCodeSystemApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeSystemApplication.class, args);
        LOGGER.info("系统模块启动成功");
    }
}

package com.troy.sync;

import com.troy.common.security.annotation.EnableCustomConfig;
import com.troy.common.security.annotation.EnableRyFeignClients;
import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 15:15:39
 * @Description: 定时任务
 * @Version: 1.0.0
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = {"com.troy"})
@MapperScan("com.troy.**.mapper")
public class LowCodeSyncApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeSyncApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeSyncApplication.class, args);
        LOGGER.info("同步数据模块");
    }
}

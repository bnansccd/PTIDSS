package com.troy.sync;

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
@SpringBootApplication
public class LowCodeSyncInsideApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(LowCodeSyncInsideApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeSyncInsideApplication.class, args);
        LOGGER.info("同步数据政务网模块");
    }
}

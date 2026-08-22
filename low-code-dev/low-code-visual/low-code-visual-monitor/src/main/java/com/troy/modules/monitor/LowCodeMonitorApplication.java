package com.troy.modules.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 15:15:09
 * @Description: 监控中心
 * @Version: 1.0.0
 */
@EnableAdminServer
@SpringBootApplication
public class LowCodeMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowCodeMonitorApplication.class, args);
        System.out.println("监控中心启动成功");
    }
}

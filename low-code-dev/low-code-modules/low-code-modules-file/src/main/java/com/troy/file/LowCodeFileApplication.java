package com.troy.file;

import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 09:9:43
 * @Description: 文件服务
 * @Version: 1.0.0
 */
@EnableCustomSwagger2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = "com.troy")
public class LowCodeFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowCodeFileApplication.class, args);
        System.out.println("文件服务模块启动成功");
    }
}

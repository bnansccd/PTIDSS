package com.troy.form;

import com.troy.common.security.annotation.EnableCustomConfig;
import com.troy.common.security.annotation.EnableRyFeignClients;
import com.troy.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 09:9:43
 * @Description: 文件服务
 * @Version: 1.0.0
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.troy"})
@MapperScan("com.troy.**.mapper")
@EnableAsync
public class LowCodeFormApplication {

    private static Logger log = LoggerFactory.getLogger(LowCodeFormApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LowCodeFormApplication.class, args);
        log.info("表单引擎模块启动成功");
    }
}

package com.troy.common.datasource.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 09:9:58
 * @Description: MyCommandLineRunner
 * @Version: 1.0.0
 */
public class MyCommandLineRunner implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //开启审计功能
//        AuditManager.setAuditEnable(true);
        //设置 SQL 审计收集器
//        MessageCollector collector = new ConsoleMessageCollector();
//        AuditManager.setMessageCollector(collector);
        //注册自定义主键
    }
}

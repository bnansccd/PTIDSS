package com.troy.form.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chenxl
 * @date 2023/11/10
 */

@Slf4j
@Configuration
public class BootThreadPoolConfig {

    // 配置核心线程数
    private static final int CORE_POOL_SIZE = 5;
    // 配置最大线程数
    private static final int MAX_POOL_SIZE = 20;
    // 配置任务队列的长度
    private static final int QUEUE_CAPACITY = 500;
    // 配置任务的空闲时间
    private static final int ALIVE_SECONDS = 600;
    // 配置线程前缀
    private static final String NAME_PREFIX = "asyncExecutorPool";

    // 自定义线程池, 起个好记的名
    @Bean(name = "asyncExecutor")
    public Executor asyncServiceExecutor() {
        log.info("初始化 springboot 线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(CORE_POOL_SIZE);
        //配置最大线程数
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        //配置队列大小
        executor.setQueueCapacity(QUEUE_CAPACITY);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(NAME_PREFIX);
        //配置线程的空闲时间
        executor.setKeepAliveSeconds(ALIVE_SECONDS);

        // RejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        log.info("springboot 线程池初始化完毕");
        return executor;
    }

}

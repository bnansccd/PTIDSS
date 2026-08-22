package com.troy.common.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: zhuQing
 * @date: 2020/11/24 0024 10:51
 * @describe: 线程池配置
 */
public class AsyncConfig implements AsyncConfigurer {

    public static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

    private static final int num = Runtime.getRuntime().availableProcessors();

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数据
        executor.setCorePoolSize(num*2);
        //最大线程数
        executor.setMaxPoolSize(num * 5);
        //线程池队列容量
        executor.setQueueCapacity(num * 1000);
        //设备线程活跃时间（秒）
        executor.setKeepAliveSeconds(50);
        //线程池名称前缀
        executor.setThreadNamePrefix("my-thread-");
        //当线程池达到最大值如何处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //等待所有线程任务结束再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    /**
     * 异步任务中异常处理
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            LOGGER.error("class#{}#mehtod", method.getDeclaringClass().getName(), method.getName());
            LOGGER.error("type:{}", ex.getClass().getName());
            LOGGER.error("exception:", ex.getMessage(), ex);
        };
    }

    /**
     * 保证定时任务时，同步进行
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //核心线程数据
        int num = Runtime.getRuntime().availableProcessors();
        scheduler.setPoolSize(num * 2);
        scheduler.setThreadNamePrefix("my-scheduled-task-");
        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        scheduler.setAwaitTerminationSeconds(60);
        // 线程池对拒绝任务的处理策略,当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return scheduler;
    }
}

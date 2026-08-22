package com.troy.common.redis.configure;

import com.troy.common.core.utils.StringUtils;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 分布式锁配置，单机模式
 * @Author: zhuQing
 * @Date: 2024/5/21 10:19
 * @Version: 1.0
 **/
@Data
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private String redisPort;

    @Value("${spring.redis.password:}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (StringUtils.isNotBlank(password)) {
            config.useSingleServer()
                    .setAddress("redis://" + redisHost + ":" + redisPort).setPassword(password);
        } else {
            config.useSingleServer()
                    .setAddress("redis://" + redisHost + ":" + redisPort);
        }
        return Redisson.create(config);
    }

}

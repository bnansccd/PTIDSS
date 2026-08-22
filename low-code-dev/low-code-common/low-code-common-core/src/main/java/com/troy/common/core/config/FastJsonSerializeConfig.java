package com.troy.common.core.config;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author sym
 * @since 2024/7/30 10:10
 */
@Configuration
public class FastJsonSerializeConfig {

    @Bean("pascalCasSerializeConfig")
    @Scope("singleton")
    public SerializeConfig pascalCasSerializeConfig() {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.PascalCase;
        return config;
    }

}

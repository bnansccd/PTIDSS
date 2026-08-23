package com.troy.common.core.config;

import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
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
    public ObjectWriterProvider pascalCasSerializeConfig() {
        // fastjson2 序列化配置：与 fastjson 1.x SerializeConfig 对应的实现为 ObjectWriterProvider
        return new ObjectWriterProvider(PropertyNamingStrategy.PascalCase);
    }

}

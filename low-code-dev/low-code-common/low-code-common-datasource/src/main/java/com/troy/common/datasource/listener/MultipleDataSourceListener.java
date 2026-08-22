package com.troy.common.datasource.listener;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.troy.common.datasource.entity.MultipleDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 多数据源配置
 * @Author: zhuQing
 * @Date: 2024/8/26 14:43
 * @Version: 1.0
 **/
@Slf4j
@Configuration
public class MultipleDataSourceListener implements ApplicationListener<MultipleDataSourceEvent> {

    @Override
    public void onApplicationEvent(MultipleDataSourceEvent event) {
        MultipleDataSource source = (MultipleDataSource) event.getSource();
        FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        log.info("多数据源注册：{}", source.getKey());
        dataSource.addDataSource(source.getKey(), source.getDruidDataSource());
    }
}

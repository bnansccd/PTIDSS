package com.troy.common.datasource.config;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2024/8/2 13:59
 * @Version: 1.0
 **/
public class MyConfigurationCustomizer implements ConfigurationCustomizer {
    @Override
    public void customize(FlexConfiguration configuration) {
//        configuration.setLogImpl(StdOutImpl.class);
    }
}

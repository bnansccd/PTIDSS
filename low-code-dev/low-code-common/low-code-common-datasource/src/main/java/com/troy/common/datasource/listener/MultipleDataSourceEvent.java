package com.troy.common.datasource.listener;

import com.troy.common.datasource.entity.MultipleDataSource;
import org.springframework.context.ApplicationEvent;

/**
 * @Description: 多数据源配置
 * @Author: zhuQing
 * @Date: 2024/8/26 14:42
 * @Version: 1.0
 **/
public class MultipleDataSourceEvent extends ApplicationEvent {

    public MultipleDataSourceEvent(MultipleDataSource source) {
        super(source);
    }
}

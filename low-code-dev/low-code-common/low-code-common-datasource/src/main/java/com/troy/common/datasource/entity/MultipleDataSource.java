package com.troy.common.datasource.entity;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 多数据源配置
 * @Author: zhuQing
 * @Date: 2024/8/26 14:45
 * @Version: 1.0
 **/
@Data
public class MultipleDataSource implements Serializable {

    private String key;

    private DruidDataSource druidDataSource;
}

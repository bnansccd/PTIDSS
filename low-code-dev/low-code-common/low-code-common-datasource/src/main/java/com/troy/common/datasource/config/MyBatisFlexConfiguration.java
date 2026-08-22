package com.troy.common.datasource.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.common.datasource.listener.MyInsertListener;
import com.troy.common.datasource.listener.MyLogicDeleteProcessor;
import com.troy.common.datasource.listener.MySetListener;
import com.troy.common.datasource.listener.MyUpdateListener;
import org.springframework.core.env.Environment;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/31 10:10:31
 * @Description: MyBatisFlexConfiguration
 * @Version: 1.0.0
 */
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    public static final String REGISTER_IP = SpringUtils.getBean(Environment.class).getProperty("spring.cloud.nacos.discovery.server-addr");

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        //注册插入监听器
        MyInsertListener myInsertListener = new MyInsertListener();
        globalConfig.registerInsertListener(myInsertListener, BaseEntity.class);
        //注册修改监听器
        MyUpdateListener myUpdateListener = new MyUpdateListener();
        globalConfig.registerUpdateListener(myUpdateListener, BaseEntity.class);
        //注册查询监听器
        MySetListener mySetListener = new MySetListener();
        globalConfig.registerSetListener(mySetListener, BaseEntity.class);
        //配置逻辑删除
        globalConfig.setNormalValueOfLogicDelete(0);
        globalConfig.setDeletedValueOfLogicDelete(1);

        LogicDeleteManager.setProcessor(new MyLogicDeleteProcessor());
        KeyGeneratorFactory.register("myIdWork", new IdWorkGenerator());
    }
}

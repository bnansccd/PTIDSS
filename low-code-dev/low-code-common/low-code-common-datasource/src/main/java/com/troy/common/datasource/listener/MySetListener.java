package com.troy.common.datasource.listener;

import com.mybatisflex.annotation.SetListener;
import com.troy.common.datasource.config.MyBatisFlexConfiguration;
import com.troy.common.datasource.strategy.DeviceRegisterFactory;


/**
 * @Auther: zhuqing
 * @Date: 2023/8/31 10:10:51
 * @Description: MySetListener
 * @Version: 1.0.0
 */
public class MySetListener implements SetListener {

    @Override
    public Object onSet(Object o, String property, Object value) {
        return DeviceRegisterFactory.decryptData(o, property, value);
    }
}

package com.troy.common.datasource.strategy;

import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.config.MyBatisFlexConfiguration;
import com.troy.common.datasource.strategy.zxUniRoad.ZXUniRoadDeviceRegisterStrategy;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2026/4/2 10:33
 * @Version: 1.0
 **/
public class DeviceRegisterFactory {


    public static void dataEncryptionAndConsistency(Object o) {
        if (StringUtils.isNotNull(o)){
            DeviceRegisterStrategy deviceRegisterStrategy = getDeviceRegisterStrategy();
            if (StringUtils.isNotNull(deviceRegisterStrategy)) {
                deviceRegisterStrategy.dataEncryptionAndConsistency(o);
            }
        }
    }

    public static Object decryptData(Object o, String property, Object value) {
        if (StringUtils.isNotNull(value)){
            DeviceRegisterStrategy deviceRegisterStrategy = getDeviceRegisterStrategy();
            if (StringUtils.isNotNull(deviceRegisterStrategy)) {
                return deviceRegisterStrategy.decryptData(o, property, value);
            }
        }
        return value;
    }

    public static String encryptData(String value) {
        if (StringUtils.isNotBlank(value)){
            DeviceRegisterStrategy deviceRegisterStrategy = getDeviceRegisterStrategy();
            if (StringUtils.isNotNull(deviceRegisterStrategy)) {
                return deviceRegisterStrategy.encryptData(value);
            }
        }
        return value;
    }


    public static DeviceRegisterStrategy getDeviceRegisterStrategy() {
        String ip = MyBatisFlexConfiguration.REGISTER_IP;

        // 判空处理
        if (ip == null || ip.isEmpty()) {
            ip = "127.0.0.1"; // 默认值
        }

        switch (ip) {
            case "172.31.1.136:8848":
                return new ZXUniRoadDeviceRegisterStrategy();
            default:
                return null;
        }
    }
}

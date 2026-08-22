package com.troy.common.datasource.strategy;

/**
 * @Description: 用于实现不同项目数据库操作的策略
 * @Author: zhuQing
 * @Date: 2026/4/2 10:06
 * @Version: 1.0
 **/
public interface DeviceRegisterStrategy {

    /**
     * 数据加密及一致性
     */
    void dataEncryptionAndConsistency(Object o);

    /**
     * 解密数据
     */
    Object decryptData(Object o, String property, Object value);

    /**
     * 加密数据
     */
    String encryptData(String value);
}

package com.troy.common.datasource.strategy.zxUniRoad;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.mask.MaskManager;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.reflect.ReflectUtils;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.annotation.Encrypted;
import com.troy.common.datasource.strategy.DeviceRegisterStrategy;
import com.troy.common.datasource.utils.PasswordTestConfig;
import com.troy.uniRoad.RemoteFbDataConsistencyService;
import com.troy.uniRoad.domain.DTO.FbDataConsistencyDTO;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 忠县非标道路一件事
 * @Author: zhuQing
 * @Date: 2026/4/2 10:11
 * @Version: 1.0
 **/
@Slf4j
public class ZXUniRoadDeviceRegisterStrategy implements DeviceRegisterStrategy {
    @Override
    public void dataEncryptionAndConsistency(Object o) {
        //字段加密
        Class<?> aClass = ReflectUtils.getUserClass(o);
        Field[] declaredFields = aClass.getDeclaredFields();
        List<FbDataConsistencyDTO> dtos = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            Annotation[] annotations = declaredField.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Encrypted.class) {
                    try {
                        Object fieldValue = ReflectUtils.getFieldValue(o, declaredField.getName());
                        String value = StringUtils.valueOf(fieldValue);
                        if (StringUtils.isNotBlank(value)) {
                            log.info("调用SM4加密，原始值 {}", value);
                            String s = PasswordTestConfig.symmetricEncrypt(value);
                            log.info("调用SM4加密，加密后值 {}", s);
                            ReflectUtils.setFieldValue(o, declaredField.getName(), s);

                        }
                    } catch (Exception e) {
                        log.error("字段加密异常", e);
                        throw new ServiceException("字段加密异常");
                    }
                }

                // 添加字段加密校验
                if (annotation.annotationType() == Consistency.class) {
                    try {
                        Long id = ReflectUtils.getFieldValue(o, "id");
                        Object fieldValue = ReflectUtils.getFieldValue(o, declaredField.getName());
                        String value = StringUtils.valueOf(fieldValue);

                        FbDataConsistencyDTO fbDataConsistencyDTO = new FbDataConsistencyDTO();
                        DruidDataSource dataSource = SpringUtils.getBean(DruidDataSource.class);
                        fbDataConsistencyDTO.setDbType(dataSource.getDbType());
                        fbDataConsistencyDTO.setDriverClassName(dataSource.getDriverClassName());
                        fbDataConsistencyDTO.setUrl(dataSource.getUrl());
                        fbDataConsistencyDTO.setUsername(dataSource.getUsername());
                        fbDataConsistencyDTO.setPassword(dataSource.getPassword());
                        fbDataConsistencyDTO.setInitialSize(dataSource.getInitialSize());
                        fbDataConsistencyDTO.setMinIdle(dataSource.getMinIdle());
                        fbDataConsistencyDTO.setMaxActive(dataSource.getMaxActive());
                        fbDataConsistencyDTO.setMaxWait((int) dataSource.getMaxWait());
                        fbDataConsistencyDTO.setTimeBetweenEvictionRunsMillis((int) dataSource.getTimeBetweenEvictionRunsMillis());
                        fbDataConsistencyDTO.setMinEvictableIdleTimeMillis((int) dataSource.getMinEvictableIdleTimeMillis());
                        fbDataConsistencyDTO.setValidationQuery(dataSource.getValidationQuery());
                        fbDataConsistencyDTO.setTestWhileIdle(dataSource.isTestWhileIdle());
                        fbDataConsistencyDTO.setTestOnBorrow(dataSource.isTestOnBorrow());
                        fbDataConsistencyDTO.setTestOnReturn(dataSource.isTestOnReturn());
                        fbDataConsistencyDTO.setMaxPoolConnectionSize(dataSource.getMaxPoolPreparedStatementPerConnectionSize());
                        fbDataConsistencyDTO.setFilters("stat,wall,slf4j");
                        fbDataConsistencyDTO.setConnectionProperties("druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000");

                        Table table = aClass.getAnnotation(Table.class);
                        fbDataConsistencyDTO.setTableName(table.value());
                        fbDataConsistencyDTO.setTableDesc(table.comment());
                        Column column = declaredField.getAnnotation(Column.class);
                        String value1 = column.value();
                        if (StringUtils.isBlank(value1)) {
                            value1 = StringUtils.toCamelCase(declaredField.getName());
                        }
                        fbDataConsistencyDTO.setColumnName(value1);
                        fbDataConsistencyDTO.setColumnDesc(column.comment());
                        if (StringUtils.isNotBlank(value)) {
                            log.info("调用SM3数据完整性，原始值 {}", value);
                            String hmac = PasswordTestConfig.hmac(value);
                            log.info("调用SM3数据完整性，加密后值 {}", hmac);
                            fbDataConsistencyDTO.setSm3Value(hmac);
                        }
                        fbDataConsistencyDTO.setAssociationId(id);
                        dtos.add(fbDataConsistencyDTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ServiceException(e.getMessage());
                    }
                }
            }

        }
        if (StringUtils.isNotEmpty(dtos)) {
            RemoteFbDataConsistencyService remoteFbDataConsistencyService = SpringUtils.getBean(RemoteFbDataConsistencyService.class);
            log.info("调用数据一致性服务:{}", JSON.toJSONString(dtos));
            ResultVO resultVO = remoteFbDataConsistencyService.insertFbDataConsistency(dtos, SecurityConstants.INNER);
            if (!ResultVO.isSuccess(resultVO)) {
                log.error("数据一致性服务调用失败:{}", resultVO.getMsg());
            }
        }
    }

    @Override
    public Object decryptData(Object o, String property, Object value) {
        //字段解密
        try {
            Class<?> aClass = ReflectUtils.getUserClass(o);
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (StringUtils.equals(declaredField.getName(), property)) {
                    Annotation[] annotations = declaredField.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType() == Encrypted.class) {
                            String s = StringUtils.valueOf(value);
                            if (StringUtils.isNotBlank(s)) {
                                return PasswordTestConfig.symmetricDecrypt(s);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("数据解密异常:{}", e.getMessage());
        }
        return value;
    }

    @Override
    public String encryptData(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = PasswordTestConfig.symmetricEncrypt(value);
        }
        return value;
    }
}

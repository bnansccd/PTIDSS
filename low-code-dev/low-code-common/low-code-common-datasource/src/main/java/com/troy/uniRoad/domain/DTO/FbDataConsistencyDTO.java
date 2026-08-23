package com.troy.uniRoad.domain.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据一致性上报 DTO
 * 原定义于私有 artifact com.troy:uniRoad-api-system（中移物联网 API），
 * 私有 nexus 不可达后平替为内部定义，字段与原接口完全一致。
 *
 * @Author: zhuQing
 * @Date: 2026/4/2 10:11
 */
@Data
public class FbDataConsistencyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库类型 */
    private String dbType;
    /** JDBC 驱动类名 */
    private String driverClassName;
    /** 数据源连接 URL */
    private String url;
    /** 数据源用户名 */
    private String username;
    /** 数据源密码 */
    private String password;
    /** 初始连接数 */
    private Integer initialSize;
    /** 最小空闲连接数 */
    private Integer minIdle;
    /** 最大活跃连接数 */
    private Integer maxActive;
    /** 获取连接最大等待时间（毫秒） */
    private Integer maxWait;
    /** 空闲连接回收检测间隔（毫秒） */
    private Integer timeBetweenEvictionRunsMillis;
    /** 连接最小空闲时间（毫秒） */
    private Integer minEvictableIdleTimeMillis;
    /** 校验 SQL */
    private String validationQuery;
    /** 空闲时是否校验 */
    private boolean testWhileIdle;
    /** 借出时是否校验 */
    private boolean testOnBorrow;
    /** 归还时是否校验 */
    private boolean testOnReturn;
    /** 池中 PreparedStatement 最大数量 */
    private Integer maxPoolConnectionSize;
    /** 过滤器配置 */
    private String filters;
    /** 连接属性配置 */
    private String connectionProperties;
    /** 表名 */
    private String tableName;
    /** 表描述 */
    private String tableDesc;
    /** 列名 */
    private String columnName;
    /** 列描述 */
    private String columnDesc;
    /** SM3 完整性校验值 */
    private String sm3Value;
    /** 关联业务主键 */
    private Long associationId;
}

package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 16:16:59
 * @Description: 租户表
 * @Version: 1.0.0
 */
@Data
@Table("t_sys_tenant")
public class SysTenantEntity extends TBaseEntity {

    /**
     * 租户名称
     */
    @Column("name")
    private String name;

    /**
     * 租户编码
     */
    @Column("code")
    private String code;

    /**
     * 租户生效时间
     */
    @Column("start_time")
    private Date startTime;

    /**
     * 租户失效时间
     */
    @Column("end_time")
    private Date endTime;

    /**
     * 状态0启用1停用
     */
    @Column(value = "status",onInsertValue = "0")
    private String status;

}

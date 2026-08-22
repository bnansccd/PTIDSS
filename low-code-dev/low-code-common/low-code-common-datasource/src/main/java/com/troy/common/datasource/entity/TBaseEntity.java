package com.troy.common.datasource.entity;

import com.mybatisflex.annotation.Column;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/26 14:14:34
 * @Description: 租户基类
 * @Version: 1.0.0
 */
public class TBaseEntity extends BaseEntity {
    /**
     * 租户id
     */
    @Column(tenantId = true)
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}

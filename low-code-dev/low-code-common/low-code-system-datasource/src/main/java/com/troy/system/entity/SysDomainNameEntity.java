package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 *  租户域名管理。
 *
 * @author zhuqing
 * @since 2023-10-08 13:54:15
 */
@Data
@Table(value = "t_sys_domain_name")
public class SysDomainNameEntity extends TBaseEntity {

    /**
     * 域名
     */
    private String domainName;

    /**
     * 泛域名
     */
    private String universalDomainName;

    /**
     * 备案信息
     */
    private String recordInfo;

    /**
     * 备案信息跳转地址
     */
    private String recordInfoUrl;

    /**
     * 备注
     */
    private String remarks;

}

package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysDomainNameEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 13:13:58
 * @Description: DomainNameDao
 * @Version: 1.0.0
 */
public interface SysDomainNameDao extends BaseService<SysDomainNameEntity> {

    /**
     * 查询租户所拥有的域名
     *
     * @return
     */
    SysDomainNameEntity domain();

    /**
     * 验证域名与泛域名是否重复
     *
     * @param id
     * @param domainName
     * @param universalDomainName
     * @return
     */
    List<SysDomainNameEntity> validRepair(Long id, String domainName, String universalDomainName);


    /**
     * 通过租户id查询域名
     *
     * @param tenantId
     * @return
     */
    SysDomainNameEntity findByTenantId(Long tenantId);

    /**
     * 通过域名查询
     *
     * @param domainName
     * @return
     */
    SysDomainNameEntity findByDomainNameOrUniversalDomainName(String domainName);
}

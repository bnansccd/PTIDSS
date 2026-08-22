package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysTenantMenuEntity;

import java.util.List;

/**
 * 基础模块应用表 服务层。
 *
 * @author echo
 * @since 2026-02-09
 */
public interface TenantMenuDao extends BaseService<SysTenantMenuEntity> {

    /**
     * 获取
     * @param tenantId
     * @param appId
     * @return
     */
    List<SysTenantMenuEntity> findByTenantIdAndAppId(Long tenantId, Long appId);

    /**
     * 删除
     * @param tenantId
     * @param appId
     */
    void removeByTenantIdAndAppId(Long tenantId, Long appId);


    /**
     * 获取
     * @param tenantId
     * @return
     */
    List<SysTenantMenuEntity> findByTenantId(Long tenantId);


    /**
     * 获取
     * @param appId
     * @return
     */
    List<SysTenantMenuEntity> findByAppId(Long appId);

    /**
     * 获取
     * @param ids
     * @return
     */
    List<SysTenantMenuEntity> findByAppIdIn(List<Long> ids);


}
package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.TenantAppSearchDTO;
import com.troy.system.entity.SysTenantAppEntity;

import java.util.List;

/**
 * 基础模块应用表 服务层。
 *
 * @author echo
 * @since 2026-02-09
 */
public interface TenantAppDao extends BaseService<SysTenantAppEntity> {


    /**
     * 获取
     * @param appId
     * @param tenantId
     * @return
     */
    SysTenantAppEntity findTenantIdAndAppId(Long appId, Long tenantId);

    /**
     * 删除
     * @param appId
     * @param tenantId
     * @return
     */
    Boolean deleteByTenantIdAndAppId(Long appId, Long tenantId);


    /**
     * 获取
     * @param tenantAppSearchDTO
     * @return
     */
    Page<SysTenantAppEntity> findPage(TenantAppSearchDTO tenantAppSearchDTO);


    /**
     * 获取
     * @param tenantId
     * @return
     */
    List<SysTenantAppEntity> findTenantId(Long tenantId);


}
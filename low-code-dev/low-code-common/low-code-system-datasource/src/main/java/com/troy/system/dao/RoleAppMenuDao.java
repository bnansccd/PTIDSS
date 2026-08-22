package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.RoleAppMenuEntity;

import java.util.List;

/**
 * 基础模块应用表 服务层。
 *
 * @author echo
 * @since 2026-02-09
 */
public interface RoleAppMenuDao extends BaseService<RoleAppMenuEntity> {

    /**
     * 获取
     * @param roleId
     * @return
     */
    List<RoleAppMenuEntity> findByRoleId(Long roleId);


    /**
     * 获取
     * @param roleId
     * @param ids
     */
    void removeByRoleIdAndAppsIn(Long roleId, List<Long> ids);


    /**
     * 获取
     * @param roleIds
     * @return
     */
    List<RoleAppMenuEntity> findByRoleIdsIn(List<Long> roleIds);

}
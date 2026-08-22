package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysConfigQueryDTO;
import com.troy.system.entity.SysConfigEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:53
 * @Description: SysConfigDao
 * @Version: 1.0.0
 */
public interface SysConfigDao extends BaseService<SysConfigEntity> {

    /**
     * @param
     * @return
     * @author yzy
     * @description分页查询
     * @date 2022/9/12
     * @version
     */
    Page<SysConfigEntity> getSysConfigPage(SysConfigQueryDTO dto);

    /**
     * 通过一批key查询配置，为空就查询所有
     *
     * @param configKeys
     * @return
     */
    List<SysConfigEntity> findBySysConfigByConfigKeyIn(List<String> configKeys);

    /**
     * 通过租户id查询配置
     *
     * @param tenantId
     * @return
     */
    List<SysConfigEntity> findByTenantId(Long tenantId);
}

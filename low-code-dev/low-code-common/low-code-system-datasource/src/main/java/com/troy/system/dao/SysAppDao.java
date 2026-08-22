package com.troy.system.dao;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysAppQueryDTO;
import com.troy.system.entity.SysAppEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface SysAppDao extends BaseService<SysAppEntity> {

    /**
     * 分页查询应用
     *
     * @param dto
     * @return
     */
    Page<SysAppEntity> getSysAppPage(SysAppQueryDTO dto);

    /**
     * 批量删除
     *
     * @param list
     * @return
     */
    Integer deletePatch(List<Long> list);

    /**
     * 通过一批id查询且排序
     *
     * @param appIds
     * @return
     */
    List<SysAppEntity> findByIdInOrderBySort(Collection<Long> appIds);


    /**
     * 获取
     * @param appCodes
     * @return
     */
    List<SysAppEntity> findByCodeInOrderBySort(Collection<String> appCodes);

    /**
     * 通过应用编码查询应用
     *
     * @param code
     * @return
     */
    SysAppEntity findByCode(String code);

    /**
     * 通过一批编码查询应用
     *
     * @param codes
     * @return
     */
    List<SysAppEntity> findByCodeIn(Collection<String> codes);

    /**
     * 通过租户id与一批应用编码查询应用
     *
     * @param tenantId
     * @param codes
     * @return
     */
    List<SysAppEntity> findByTenantIdAndCodeIn(Long tenantId, List<String> codes);

    /**
     * 通过租户id查询应用
     *
     * @param tenantId
     * @return
     */
    List<SysAppEntity> findByTenantId(Long tenantId);
}

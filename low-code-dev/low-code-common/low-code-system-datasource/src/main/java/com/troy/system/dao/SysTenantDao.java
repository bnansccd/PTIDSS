package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysTenantSearchDTO;
import com.troy.system.entity.SysTenantEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 17:17:29
 * @Description: SysTenantDao
 * @Version: 1.0.0
 */
public interface SysTenantDao extends BaseService<SysTenantEntity> {

    /**
     * 分页查询租户
     *
     * @param dto
     * @return
     */
    Page<SysTenantEntity> listPage(SysTenantSearchDTO dto);

    /**
     * 通过租户编码查询租户
     *
     * @param code
     * @return
     */
    SysTenantEntity findByCode(String code);

    /**
     * 查询所有的租户
     *
     * @param dto
     * @return
     */
    List<SysTenantEntity> tenantList(SysTenantSearchDTO dto);
}

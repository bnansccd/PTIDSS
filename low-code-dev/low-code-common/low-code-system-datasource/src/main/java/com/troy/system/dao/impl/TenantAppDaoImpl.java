package com.troy.system.dao.impl;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.TenantAppDao;
import com.troy.system.domain.DTO.TenantAppSearchDTO;
import com.troy.system.entity.SysTenantAppEntity;
import com.troy.system.entity.table.SysTenantAppEntityTableDef;
import com.troy.system.mapper.TenantAppMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 基础模块应用表 服务层实现。
 *
 * @author echo
 * @since 2026-02-09
 */
@Service
public class TenantAppDaoImpl extends BaseServiceImpl<TenantAppMapper, SysTenantAppEntity> implements TenantAppDao {

    @Override
    public SysTenantAppEntity getById(Serializable id) {
        return super.getById(id);
    }

    // === 增加相关 ===

    @Override
    public boolean save(SysTenantAppEntity entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<SysTenantAppEntity> entities) {
        return super.saveBatch(entities);
    }

    // === 修改相关 ===

    @Override
    public boolean updateById(SysTenantAppEntity entity) {
        return super.updateById(entity);
    }

    // === 删除相关 ===

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    @Override
    public SysTenantAppEntity findTenantIdAndAppId(Long appId, Long tenantId) {
        return this.mapper.selectOneByQuery(
                query()
                        .where(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.APP_ID.eq(appId))
                        .and(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.TENANT_ID.eq(tenantId))

        );
    }

    @Override
    public Boolean deleteByTenantIdAndAppId(Long appId, Long tenantId) {
        return remove(
                query()
                        .where(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.APP_ID.eq(appId))
                        .and(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.TENANT_ID.eq(tenantId))

        );
    }

    @Override
    public Page<SysTenantAppEntity> findPage(TenantAppSearchDTO dto) {
        return page(dto,
                query()
                        .where(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.APP_ID.eq(dto.getAppId(), StringUtils::isNotNull))
                        .and(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.TENANT_ID.eq(dto.getTenantId()))
        );
    }

    @Override
    public List<SysTenantAppEntity> findTenantId(Long tenantId) {
        return list(query().where(SysTenantAppEntityTableDef.SYS_TENANT_APP_ENTITY.TENANT_ID.eq(tenantId)));
    }
}
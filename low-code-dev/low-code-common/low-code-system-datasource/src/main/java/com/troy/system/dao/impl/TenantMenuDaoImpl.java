package com.troy.system.dao.impl;


import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.TenantMenuDao;
import com.troy.system.entity.SysTenantMenuEntity;
import com.troy.system.entity.table.SysTenantMenuEntityTableDef;
import com.troy.system.mapper.TenantMenuMapper;
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
public class TenantMenuDaoImpl extends BaseServiceImpl<TenantMenuMapper, SysTenantMenuEntity> implements TenantMenuDao {

    @Override
    public SysTenantMenuEntity getById(Serializable id) {
        return super.getById(id);
    }

    // === 增加相关 ===

    @Override
    public boolean save(SysTenantMenuEntity entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<SysTenantMenuEntity> entities) {
        return super.saveBatch(entities);
    }

    // === 修改相关 ===

    @Override
    public boolean updateById(SysTenantMenuEntity entity) {
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
    public List<SysTenantMenuEntity> findByTenantIdAndAppId(Long tenantId, Long appId) {
        return mapper.selectListByQuery(
                query()
                        .where(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.TENANT_ID.eq(tenantId))
                        .and(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.APP_ID.eq(appId))
        );
    }

    @Override
    public void removeByTenantIdAndAppId(Long tenantId, Long appId) {
        mapper.deleteByQuery(query()
                .where(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.TENANT_ID.eq(tenantId))
                .and(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.APP_ID.eq(appId))
        );
    }

    @Override
    public List<SysTenantMenuEntity> findByTenantId(Long tenantId) {
        return mapper.selectListByQuery(
                query()
                        .where(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.TENANT_ID.eq(tenantId))
        );
    }

    @Override
    public List<SysTenantMenuEntity> findByAppId(Long appId) {
        return mapper.selectListByQuery(
                query()
                        .where(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.APP_ID.eq(appId))
        );
    }

    @Override
    public List<SysTenantMenuEntity> findByAppIdIn(List<Long> ids) {
        return mapper.selectListByQuery(
                query()
                        .where(SysTenantMenuEntityTableDef.SYS_TENANT_MENU_ENTITY.APP_ID.in(ids))
        );
    }
}
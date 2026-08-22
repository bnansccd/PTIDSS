package com.troy.system.dao.impl;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysAppDao;
import com.troy.system.domain.DTO.SysAppQueryDTO;
import com.troy.system.entity.SysAppEntity;
import com.troy.system.entity.table.SysAppEntityTableDef;
import com.troy.system.mapper.SysAppMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.troy.system.entity.table.SysAppEntityTableDef.SYS_APP_ENTITY;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
@Component
public class SysAppDaoImpl extends BaseServiceImpl<SysAppMapper, SysAppEntity> implements SysAppDao {

    @Override
    public Page<SysAppEntity> getSysAppPage(SysAppQueryDTO dto) {
        return super.dataAuthorityPage(
                dto,
                QueryWrapper.create()
                        .where(SYS_APP_ENTITY.NAME.like(dto.getName(), StringUtils.isNotBlank(dto.getName())))
        );
    }


    @Override
    public Integer deletePatch(List<Long> list) {
        super.updateChain().setRaw(SysAppEntity::getDelFlag, Constants.ONE).where(SysAppEntity::getId).in(list).update();
        return list.size();
    }

    @Override
    public List<SysAppEntity> findByIdInOrderBySort(Collection<Long> appIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SysAppEntityTableDef.SYS_APP_ENTITY.ID.in(appIds, StringUtils.isNotEmpty(appIds)))
                        .orderBy(SysAppEntityTableDef.SYS_APP_ENTITY.SORT.asc())
        );
    }

    @Override
    public List<SysAppEntity> findByCodeInOrderBySort(Collection<String> appCodes) {

        boolean notEmpty = StringUtils.isNotEmpty(appCodes);
        if (!notEmpty){
            appCodes.add("XXXXX");
        }

        return super.list(
                QueryWrapper.create()
                        .where(SysAppEntityTableDef.SYS_APP_ENTITY.CODE.in(appCodes, StringUtils.isNotEmpty(appCodes)))
                        .orderBy(SysAppEntityTableDef.SYS_APP_ENTITY.SORT.asc())
        );
    }

    @Override
    public SysAppEntity findByCode(String code) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SysAppEntityTableDef.SYS_APP_ENTITY.CODE.eq(code))
        );
    }

    @Override
    public List<SysAppEntity> findByCodeIn(Collection<String> codes) {
        return super.list(
                QueryWrapper.create()
                        .where(SysAppEntityTableDef.SYS_APP_ENTITY.CODE.in(codes))
        );
    }

    @Override
    public List<SysAppEntity> findByTenantIdAndCodeIn(Long tenantId, List<String> codes) {
        List<SysAppEntity> sysAppEntities = new ArrayList<>();
        try {
            TenantManager.ignoreTenantCondition();
            sysAppEntities = super.list(
                    QueryWrapper.create()
                            .and(SysAppEntityTableDef.SYS_APP_ENTITY.CODE.in(codes))
            );
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return sysAppEntities;
    }

    @Override
    public List<SysAppEntity> findByTenantId(Long tenantId) {
        List<SysAppEntity> sysAppEntities = new ArrayList<>();
        try {
            TenantManager.ignoreTenantCondition();
            sysAppEntities = super.list(
                    QueryWrapper.create()
            );
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return sysAppEntities;
    }

}

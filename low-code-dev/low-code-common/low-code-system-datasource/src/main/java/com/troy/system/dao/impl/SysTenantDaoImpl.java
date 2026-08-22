package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysTenantDao;
import com.troy.system.domain.DTO.SysTenantSearchDTO;
import com.troy.system.entity.SysTenantEntity;
import com.troy.system.mapper.SysTenantMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysTenantEntityTableDef.SYS_TENANT_ENTITY;


/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 17:17:29
 * @Description: SysTenantDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysTenantDaoImpl extends BaseServiceImpl<SysTenantMapper, SysTenantEntity> implements SysTenantDao {


    @Override
    public Page<SysTenantEntity> listPage(SysTenantSearchDTO dto) {
        return super.dataAuthorityPage(
                dto,
                QueryWrapper.create()
                        .where(SYS_TENANT_ENTITY.NAME.like(dto.getName()))
                        .and(SYS_TENANT_ENTITY.CODE.like(dto.getCode()))
                        .and(
                                SYS_TENANT_ENTITY.START_TIME.ge(dto.getStartTime())
                                        .and(SYS_TENANT_ENTITY.START_TIME.le(dto.getEndTime()))
                                        .or(SYS_TENANT_ENTITY.END_TIME.ge(dto.getStartTime())
                                                .and(SYS_TENANT_ENTITY.END_TIME.le(dto.getEndTime()))
                                        )
                        )
                        .and(SYS_TENANT_ENTITY.STATUS.eq(dto.getStatus()))
        );
    }

    @Override
    public SysTenantEntity findByCode(String code) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_TENANT_ENTITY.CODE.eq(code))
        );
    }

    @Override
    public List<SysTenantEntity> tenantList(SysTenantSearchDTO dto) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_TENANT_ENTITY.NAME.like(dto.getName()))
                        .and(SYS_TENANT_ENTITY.CODE.like(dto.getCode()))
                        .and(
                                SYS_TENANT_ENTITY.START_TIME.ge(dto.getStartTime())
                                        .and(SYS_TENANT_ENTITY.START_TIME.le(dto.getEndTime()))
                                        .or(SYS_TENANT_ENTITY.END_TIME.ge(dto.getStartTime())
                                                .and(SYS_TENANT_ENTITY.END_TIME.le(dto.getEndTime()))
                                        )
                        )
                        .and(SYS_TENANT_ENTITY.STATUS.eq(dto.getStatus()))
        );
    }
}

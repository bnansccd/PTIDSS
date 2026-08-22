package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysDomainNameDao;
import com.troy.system.entity.SysDomainNameEntity;
import com.troy.system.mapper.SysDomainNameMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysDomainNameEntityTableDef.SYS_DOMAIN_NAME_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 13:13:58
 * @Description: DomainNameDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysDomainNameDaoImpl extends BaseServiceImpl<SysDomainNameMapper, SysDomainNameEntity> implements SysDomainNameDao {


    @Override
    public SysDomainNameEntity domain() {
        return super.getOne(QueryWrapper.create());
    }

    @Override
    public List<SysDomainNameEntity> validRepair(Long id, String domainName, String universalDomainName) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_DOMAIN_NAME_ENTITY.DOMAIN_NAME.eq(domainName).or(SYS_DOMAIN_NAME_ENTITY.UNIVERSAL_DOMAIN_NAME.eq(universalDomainName)))
                        .and(SYS_DOMAIN_NAME_ENTITY.ID.ne(id))
        );
    }

    @Override
    public SysDomainNameEntity findByTenantId(Long tenantId) {
        SysDomainNameEntity sysDomainNameEntity = null;
        try {
            TenantManager.ignoreTenantCondition();
            sysDomainNameEntity = super.getOne(
                    QueryWrapper.create()
                            .where(SYS_DOMAIN_NAME_ENTITY.TENANT_ID.eq(tenantId))
            );
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return sysDomainNameEntity;
    }

    @Override
    public SysDomainNameEntity findByDomainNameOrUniversalDomainName(String domainName) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_DOMAIN_NAME_ENTITY.DOMAIN_NAME.eq(domainName).or(SYS_DOMAIN_NAME_ENTITY.UNIVERSAL_DOMAIN_NAME.eq(domainName)))
        );
    }
}

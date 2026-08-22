package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysRoleDao;
import com.troy.system.domain.DTO.SysRoleQueryDTO;
import com.troy.system.entity.SysRoleEntity;
import com.troy.system.mapper.SysRoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysRoleEntityTableDef.SYS_ROLE_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:22
 * @Description: SysRoleDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysRoleDaoImpl extends BaseServiceImpl<SysRoleMapper, SysRoleEntity> implements SysRoleDao {
    @Override
    public List<SysRoleEntity> listAll() {
        return super.list(
                QueryWrapper.create()
                        .orderBy(SysRoleEntity::getSort).asc()
        );
    }

    @Override
    public Page<SysRoleEntity> getSysRolePage(SysRoleQueryDTO dto) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SysRoleEntity::getRoleName).eq(dto.getRoleName(), StringUtils.isNotBlank(dto.getRoleName()));
        return super.dataAuthorityPage(dto, queryWrapper);
    }

    @Override
    public SysRoleEntity findByRoleCode(String roleCode) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_ROLE_ENTITY.ROLE_CODE.eq(roleCode))
        );
    }

    @Override
    public SysRoleEntity findMaxSort() {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_ROLE_ENTITY.SORT.isNotNull())
                        .orderBy(SYS_ROLE_ENTITY.SORT.desc()).limit(Constants.ONE)
        );
    }

    @Override
    public SysRoleEntity findByTenantIdAndRoleCode(Long tenantId, String roleCode) {
        SysRoleEntity sysRoleEntity = null;
        try {
            TenantManager.ignoreTenantCondition();
            sysRoleEntity = super.getOne(
                    QueryWrapper.create()
                            .where(SYS_ROLE_ENTITY.TENANT_ID.eq(tenantId))

            );
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return sysRoleEntity;
    }
}

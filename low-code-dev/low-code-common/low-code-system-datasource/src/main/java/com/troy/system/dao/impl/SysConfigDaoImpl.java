package com.troy.system.dao.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysConfigDao;
import com.troy.system.domain.DTO.SysConfigQueryDTO;
import com.troy.system.entity.SysConfigEntity;
import com.troy.system.entity.table.SysConfigEntityTableDef;
import com.troy.system.mapper.SysConfigMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysConfigEntityTableDef.SYS_CONFIG_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 11:11:53
 * @Description: SysConfigDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysConfigDaoImpl extends BaseServiceImpl<SysConfigMapper, SysConfigEntity> implements SysConfigDao {

    @Override
    public Page<SysConfigEntity> getSysConfigPage(SysConfigQueryDTO dto) {
        return super.dataAuthorityPage(
                dto,
                QueryWrapper.create()
                        .where(SysConfigEntity::getConfigName).like(dto.getConfigName(), StringUtils.isNotBlank(dto.getConfigName()))
        );
    }

    @Override
    public List<SysConfigEntity> findBySysConfigByConfigKeyIn(List<String> configKeys) {
        return super.list(
                QueryWrapper.create()
                        .where(SysConfigEntityTableDef.SYS_CONFIG_ENTITY.CONFIG_KEY.in(configKeys, StringUtils.isNotEmpty(configKeys)))
        );
    }

    @Override
    public List<SysConfigEntity> findByTenantId(Long tenantId) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_CONFIG_ENTITY.TENANT_ID.eq(tenantId))
        );
    }
}

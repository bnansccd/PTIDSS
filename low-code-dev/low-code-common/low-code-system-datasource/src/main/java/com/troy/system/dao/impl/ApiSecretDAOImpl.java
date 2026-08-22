package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.ApiSecretDAO;
import com.troy.system.entity.ApiSecretEntity;
import com.troy.system.entity.table.ApiSecretEntityTableDef;
import com.troy.system.mapper.ApiSecretMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sym
 * @description
 * @date 2023/12/1 10:22
 */
@Component
public class ApiSecretDAOImpl extends BaseServiceImpl<ApiSecretMapper, ApiSecretEntity> implements ApiSecretDAO {

    @Autowired
    private ApiSecretMapper mapper;

    @Override
    public ApiSecretEntity getOneByOrgId(String orgId) {
        QueryWrapper queryWrapper = new QueryWrapper().where(ApiSecretEntityTableDef.API_SECRET_ENTITY.ORG_ID.eq(orgId))
                .from(ApiSecretEntityTableDef.API_SECRET_ENTITY);
        return mapper.selectOneWithRelationsByQuery(queryWrapper);
    }

}

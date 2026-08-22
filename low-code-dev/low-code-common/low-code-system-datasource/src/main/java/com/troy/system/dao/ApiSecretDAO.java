package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.ApiSecretEntity;

/**
 * @author sym
 * @description
 * @date 2023/12/1 10:21
 */
public interface ApiSecretDAO extends BaseService<ApiSecretEntity> {

    /**
     * 通过orgId获取实体类
     */
    ApiSecretEntity getOneByOrgId(String orgId);

}

package com.troy.system.service;


// import com.troy.system.entity.ApiSecretEntity;

import com.troy.system.entity.ApiSecretEntity;

import java.util.List;

/**
 * @author sym
 * @description
 * @date 2023/12/1 10:19
 */

public interface ApiSecretService {

    /**
     * 通过orgId获取实体类
     */
    ApiSecretEntity getOneByOrgId(String orgId);


    List<ApiSecretEntity> getAll();

}

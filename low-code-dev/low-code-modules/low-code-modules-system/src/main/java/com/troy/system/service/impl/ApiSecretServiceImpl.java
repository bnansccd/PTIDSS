package com.troy.system.service.impl;


import com.troy.system.dao.ApiSecretDAO;
import com.troy.system.entity.ApiSecretEntity;
import com.troy.system.service.ApiSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sym
 * @description
 * @date 2023/12/1 10:19
 */
@Service
public class ApiSecretServiceImpl implements ApiSecretService {

    @Autowired
    private ApiSecretDAO apiSecretDao;


    @Override
    public ApiSecretEntity getOneByOrgId(String orgId) {
        return apiSecretDao.getOneByOrgId(orgId);
    }

    @Override
    public List<ApiSecretEntity> getAll() {
        return apiSecretDao.list();
    }

}

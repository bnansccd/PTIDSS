package com.troy.sync.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.sync.entity.TargetEntity;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 10:25
 */
public interface TargetDao extends BaseService<TargetEntity> {

    /**
     * 获取
     * @param target
     * @return
     */
    TargetEntity findByTarget(String target);

}

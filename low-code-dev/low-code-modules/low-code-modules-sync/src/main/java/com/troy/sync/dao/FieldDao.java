package com.troy.sync.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.sync.entity.FieldEntity;

import java.util.List;

public interface FieldDao extends BaseService<FieldEntity> {

    /**
     * 获取
     * @param tableId
     * @return
     */
    List<FieldEntity> findByTableId(Long tableId);

}

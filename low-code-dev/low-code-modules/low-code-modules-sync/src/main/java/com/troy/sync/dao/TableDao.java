package com.troy.sync.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.sync.entity.TableEntity;

public interface TableDao extends BaseService<TableEntity> {

    /**
     * 获取
     * @param tableName
     * @param sourceId
     * @return
     */
    TableEntity findByTableNameAndSourceId(String tableName, Long sourceId);


}

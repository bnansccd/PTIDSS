package com.troy.form.dao;


import com.troy.common.datasource.service.BaseService;
import com.troy.form.entity.DbTableEntity;

import java.util.List;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface DbTableDao extends BaseService<DbTableEntity> {


    /**
     * 获取table
     * @return
     */
    List<DbTableEntity> findAllTable();

    /**
     * 获取name
     * @param tableName
     * @param tableId
     * @return
     */
    DbTableEntity findByTable(String tableName, Long tableId);


    /**
     * 获取实体
     * @param tableName
     * @return
     */
    DbTableEntity findByTable(String tableName);

}

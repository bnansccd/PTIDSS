package com.troy.form.dao;


import com.troy.common.datasource.service.BaseService;
import com.troy.form.entity.DbColumnEntity;

import java.util.Collection;
import java.util.List;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface DbColumnDao extends BaseService<DbColumnEntity> {

    /**
     * 获取所有的表
     * @return
     */
    List<DbColumnEntity> findAll();


    /**
     * 根据tableName
     * @param tableName
     * @return
     */
    List<DbColumnEntity> findByTableName(String tableName);


    /**
     *
     * @param tableId
     * @return
     */
    List<DbColumnEntity> findByTableId(Long tableId);


    /**
     * 计算有值
     * @return
     */
    Long countByTable(String tableName);

    /**
     * 根据表获取
     * @param list
     * @return
     */
    List<DbColumnEntity> findByTableIdIn(Collection<Long> list);
}

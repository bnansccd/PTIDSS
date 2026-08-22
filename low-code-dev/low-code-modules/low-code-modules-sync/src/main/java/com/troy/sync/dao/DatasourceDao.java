package com.troy.sync.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.sync.entity.DatasourceEntity;

import java.util.List;

public interface DatasourceDao extends BaseService<DatasourceEntity> {


    /**
     * 获取所有
     * @return
     */
    List<DatasourceEntity> findAll();


    /**
     * 获取
     * @param list
     * @return
     */
    List<DatasourceEntity> findBySourceIdIn(List<Long> list);


    List<DatasourceEntity> findByTargetIn(List<String> list);

    /**
     * 获取
     * @param target
     * @return
     */
    DatasourceEntity findByTarget(String target);

}

package com.troy.form.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.form.entity.DbErRelationEntity;

import java.util.List;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public interface DbErRelationDao extends BaseService<DbErRelationEntity> {

    /**
     * 删除
     * @param id
     */
    void removeByErId(Long id);


    /**
     * 获取er
     * @param erId
     * @return
     */
    List<DbErRelationEntity> listByErId(Long erId);
}

package com.troy.form.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.form.entity.DbErEntity;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public interface DbErDao extends BaseService<DbErEntity> {

    /**
     * 是否存在当前模型
     * @param id
     * @param name
     * @param erModelMark
     * @return
     */
    boolean exists(Long id, String name, String erModelMark);
}

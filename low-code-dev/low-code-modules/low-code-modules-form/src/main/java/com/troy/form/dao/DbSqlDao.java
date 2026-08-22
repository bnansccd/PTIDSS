package com.troy.form.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.form.domain.DTO.DbSqlSearchDTO;
import com.troy.form.entity.DbSqlEntity;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public interface DbSqlDao extends BaseService<DbSqlEntity> {

    /**
     * 是否存在当前模型
     * @param id
     * @param name
     * @param code
     * @return
     */
    boolean exists(Long id, String name, String code);

    /**
     * 获取分页
     * @param dto
     * @return
     */
    Page<DbSqlEntity> findPage(DbSqlSearchDTO dto);
}

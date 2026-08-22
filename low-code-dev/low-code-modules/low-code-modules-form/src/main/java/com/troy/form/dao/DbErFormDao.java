package com.troy.form.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.form.domain.DTO.DbErFormSearchDTO;
import com.troy.form.entity.DbErFormEntity;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public interface DbErFormDao extends BaseService<DbErFormEntity> {

    /**
     * 是否存在
     *
     * @param id
     * @param name
     * @param mark
     * @return
     */
    boolean exists(Long id, String name, String mark);

    /**
     * 获取分页
     *
     * @param dto
     * @return
     */
    Page<DbErFormEntity> findPage(DbErFormSearchDTO dto);

    /**
     * 根据mark
     *
     * @param mark
     * @return
     */
    DbErFormEntity findByMark(String mark);


}
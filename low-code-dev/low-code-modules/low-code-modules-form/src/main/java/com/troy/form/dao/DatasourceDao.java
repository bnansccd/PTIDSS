package com.troy.form.dao;


import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.form.domain.DTO.DatasourceSearchDTO;
import com.troy.form.entity.DatasourceEntity;

import java.util.List;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface DatasourceDao extends BaseService<DatasourceEntity> {

    /**
     * 判断是否有相同的
     * @param identifier
     * @return
     */
    DatasourceEntity findByIdentification(String identifier, String name);

    /**
     * 获取所有数据源
     * @return
     */
    List<DatasourceEntity> findAll();

    /**
     * 获取分页
     * @param dto
     * @return
     */
    Page<DatasourceEntity> findPage(DatasourceSearchDTO dto);
}

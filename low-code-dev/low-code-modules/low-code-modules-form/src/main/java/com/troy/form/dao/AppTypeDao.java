package com.troy.form.dao;


import com.troy.common.datasource.service.BaseService;
import com.troy.form.domain.DTO.AppTypeSearchDTO;
import com.troy.form.entity.AppTypeEntity;

import java.util.List;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface AppTypeDao extends BaseService<AppTypeEntity> {

    /**
     * 根据名称获取
     * @param name
     * @return
     */
    AppTypeEntity findFirstByName(String name);

    /**
     * 获取分类
     * @param dto
     * @return
     */
    List<AppTypeEntity> findPageList(AppTypeSearchDTO dto);

}

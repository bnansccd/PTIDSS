package com.troy.form.dao;


import com.troy.common.datasource.service.BaseService;
import com.troy.form.entity.AppEntity;
import com.troy.form.domain.DTO.AppSearchDTO;

import java.util.List;


/**
 * @author chenxl
 * @Date 2023/3/14
 */
public interface AppDao extends BaseService<AppEntity> {

    /**
     * 根据名称获取
     * @param name
     * @return
     */
    AppEntity findFirstByName(String name);

    /**
     * 获取分类
     * @param dto
     * @return
     */
    List<AppEntity> findPageList(AppSearchDTO dto);
}

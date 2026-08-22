package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.form.domain.DTO.DbErDTO;
import com.troy.form.entity.DbErEntity;

/**
 *  服务层。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
public interface DbErService extends IService<DbErEntity> {

    /**
     * 新增模型
     * @param dbErDTO
     */
    void addErModel(DbErDTO dbErDTO);


    /**
     * 更新
     * @param id
     * @param dbErDTO
     */
    void updateErModel(Long id, DbErDTO dbErDTO);
}

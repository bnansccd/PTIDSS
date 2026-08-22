package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.common.core.web.VO.PageVO;
import com.troy.form.domain.DTO.DbErFormDTO;
import com.troy.form.domain.DTO.DbErFormSearchDTO;
import com.troy.form.domain.VO.DbErFormVO;
import com.troy.form.entity.DbErFormEntity;

/**
 *  服务层。
 *
 * @author chenxl
 * @since 2023-11-09 09:45:50
 */
public interface DbErFormService extends IService<DbErFormEntity> {

    /**
     * 新增表单模型
     * @param dbErFormDTO
     */
    void addForm(DbErFormDTO dbErFormDTO);

    /**
     * 更新form
     * @param id
     * @param dbErFormDTO
     */
    void updateForm(Long id, DbErFormDTO dbErFormDTO);

    /**
     * 获取分页
     * @param dto
     * @return
     */
    PageVO<DbErFormVO> findPage(DbErFormSearchDTO dto);

    /**
     * 上锁
     * @param id
     * @param status
     */
    void lockForm(Long id, String status);

}

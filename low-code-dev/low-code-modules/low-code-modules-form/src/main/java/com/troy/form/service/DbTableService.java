package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.common.core.web.VO.PageVO;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.domain.DTO.DbTableSearchDTO;
import com.troy.form.domain.DTO.TableDTO;
import com.troy.form.domain.VO.DbTableVO;

/**
 *  服务层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
public interface DbTableService extends IService<DbTableEntity> {

    /**
     * 新增表
     * @param dto
     */
    void addTable(TableDTO dto);

    /**
     * 更新
     * @param tableId
     * @param dto
     */
    void updateTable(Long tableId, TableDTO dto);

    /**
     * 是否存在
     * @param tableName
     * @param id
     * @param dbId
     * @return
     */
    boolean isExist(String tableName, Long id, Long dbId);


    /**
     * 获取表
     * @param dto
     * @return
     */
    PageVO<DbTableVO> getList(DbTableSearchDTO dto);

    /**
     * 新增表
     * @param id
     * @param tableName
     * @return
     */
    void addTable(Long id, String tableName);

    /**
     * 获取sql
     * @param id
     * @return
     */
    String getSQl(Long id);

}

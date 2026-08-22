package com.troy.form.service;

import com.mybatisflex.core.service.IService;
import com.troy.common.core.web.VO.PageVO;
import com.troy.form.domain.DTO.DbSqlDTO;
import com.troy.form.domain.DTO.DbSqlSearchDTO;
import com.troy.form.domain.VO.DbSqlVO;
import com.troy.form.entity.DbSqlEntity;

/**
 *  服务层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
public interface DbSqlService extends IService<DbSqlEntity> {

    /**
     * 新增sql
     * @param dbSqlDTO
     */
    void addSql(DbSqlDTO dbSqlDTO);

    /**
     * 更新sql
     * @param id
     * @param dbSqlDTO
     */
    void updateSql(Long id, DbSqlDTO dbSqlDTO);

    /**
     * 获取分页
     * @param searchDTO
     * @return
     */
    PageVO<DbSqlVO> getPage(DbSqlSearchDTO searchDTO);
}

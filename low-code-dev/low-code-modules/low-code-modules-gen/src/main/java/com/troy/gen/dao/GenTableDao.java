package com.troy.gen.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.entity.GenTableEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:19
 * @Description: GenTableDao
 * @Version: 1.0.0
 */
public interface GenTableDao extends BaseService<GenTableEntity> {

    /**
     * 查询数据库列表
     *
     * @param dto
     * @return
     */
    Page<GenTableEntity> dataList(DbTableDTO dto);

    /**
     * 查询据库列表
     *
     * @param tableNames
     * @return
     */
    List<GenTableEntity> selectDbTableListByNames(List<String> tableNames);

    /**
     * 通过子表schema与名称查询子表
     *
     * @param tableName
     * @return
     */
    GenTableEntity findByTableName(String tableName);

    /**
     * 查询对应的主表
     *
     * @param tableName
     * @return
     */
    List<GenTableEntity> findBySubTableName(String tableName);
}

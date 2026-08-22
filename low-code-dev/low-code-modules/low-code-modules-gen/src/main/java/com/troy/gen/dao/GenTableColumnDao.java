package com.troy.gen.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.gen.entity.GenTableColumnEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:25
 * @Description: GenTableColumnDao
 * @Version: 1.0.0
 */
public interface GenTableColumnDao extends BaseService<GenTableColumnEntity> {

    /**
     * 通过tableId查询表字段
     *
     * @param tableId
     * @return
     */
    List<GenTableColumnEntity> findByTableId(Long tableId);

    /**
     * 通过schema与table查询表字段
     *
     * @param tableName
     * @return
     */
    List<GenTableColumnEntity> selectDbTableColumnsByName(String tableName);

    /**
     * 通过tableIds删除表字段
     *
     * @param ids
     */
    Boolean deleteByTableIds(List<Long> tableIds);

    /**
     * 通过一批表id查询对应字段
     * @param tableIds
     * @return
     */
    List<GenTableColumnEntity> findByTableIds(List<Long> tableIds);
}

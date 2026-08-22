package com.troy.gen.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.gen.entity.GenTableColumnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Mapper
public interface GenTableColumnMapper extends MyBaseMapper<GenTableColumnEntity> {

    /**
     * 通过schema与table查询表字段
     *
     * @param tableName
     * @return
     */
    List<GenTableColumnEntity> selectDbTableColumnsByName(@Param("tableName") String tableName);
}

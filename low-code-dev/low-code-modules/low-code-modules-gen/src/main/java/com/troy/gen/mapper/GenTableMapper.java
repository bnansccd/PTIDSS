package com.troy.gen.mapper;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.entity.GenTableEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代码生成业务表 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Mapper
public interface GenTableMapper extends MyBaseMapper<GenTableEntity> {

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
     * @param dto
     * @return
     */
    List<GenTableEntity> selectDbTableListByNames(@Param("tableNames") List<String> tableNames);
}

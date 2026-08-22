package com.troy.form.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.form.entity.DatasourceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 招聘岗位表 Mapper 接口
 * </p>
 *
 * @author chenxl
 * @since 2023-03-13
 */
@Mapper
public interface DatasourceMapper extends MyBaseMapper<DatasourceEntity> {

}

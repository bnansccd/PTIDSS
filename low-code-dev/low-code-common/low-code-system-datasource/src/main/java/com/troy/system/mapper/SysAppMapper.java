package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysAppEntity;
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
public interface SysAppMapper extends MyBaseMapper<SysAppEntity> {

}

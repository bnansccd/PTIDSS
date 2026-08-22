package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色管理 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Mapper
public interface SysRoleMapper extends MyBaseMapper<SysRoleEntity> {

}

package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色与用户的关系表 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Mapper
public interface SysUserRoleMapper extends MyBaseMapper<SysUserRoleEntity> {

}

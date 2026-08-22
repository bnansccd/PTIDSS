package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysDepartRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 部门与角色关系表用与数据权限 Mapper 接口
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Mapper
public interface SysDepartRoleMapper extends MyBaseMapper<SysDepartRoleEntity> {

}

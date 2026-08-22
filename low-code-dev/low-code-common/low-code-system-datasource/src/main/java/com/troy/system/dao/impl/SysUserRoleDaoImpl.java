package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysUserRoleDao;
import com.troy.system.entity.SysUserRoleEntity;
import com.troy.system.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysUserRoleEntityTableDef.SYS_USER_ROLE_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:24
 * @Description: SysUserRoleDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysUserRoleDaoImpl extends BaseServiceImpl<SysUserRoleMapper, SysUserRoleEntity> implements SysUserRoleDao {

    @Override
    public List<SysUserRoleEntity> findByUserId(Long userId) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_USER_ROLE_ENTITY.USER_ID.eq(userId))
        );
    }

    @Override
    public List<SysUserRoleEntity> findByUserIdIn(List<Long> userIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_USER_ROLE_ENTITY.USER_ID.in(userIds))
        );
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysUserRoleEntity::getUserId).eq(userId)
        );
    }

    @Override
    public boolean deleteByUserId(List<Long> userIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysUserRoleEntity::getUserId).in(userIds)
        );
    }

    @Override
    public boolean deleteByRoleId(List<Long> roleIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysUserRoleEntity::getRoleId).in(roleIds)
        );
    }
}

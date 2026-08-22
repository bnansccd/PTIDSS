package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysRoleMenuDao;
import com.troy.system.entity.SysRoleMenuEntity;
import com.troy.system.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:18
 * @Description: SysRoleMenuDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysRoleMenuDaoImpl extends BaseServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity> implements SysRoleMenuDao {


    @Override
    public List<SysRoleMenuEntity> findByRoleId(Long roleId) {
        return super.list(
                QueryWrapper.create()
                        .where(SysRoleMenuEntity::getRoleId).eq(roleId)
        );
    }

    @Override
    public List<SysRoleMenuEntity> findByRoleIdIn(List<Long> roleIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SysRoleMenuEntity::getRoleId).in(roleIds)
        );
    }

    @Override
    public boolean deleteByMenuId(List<Long> menuIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysRoleMenuEntity::getMenuId).in(menuIds)
        );
    }

    @Override
    public boolean deleteByRoleId(Long roleId) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysRoleMenuEntity::getRoleId).eq(roleId)
        );
    }

    @Override
    public boolean deleteByRoleId(List<Long> roleIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysRoleMenuEntity::getRoleId).in(roleIds)
        );
    }
}

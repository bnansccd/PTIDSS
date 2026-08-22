package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysDepartRoleDao;
import com.troy.system.entity.SysDepartRoleEntity;
import com.troy.system.mapper.SysDepartRoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/11 16:16:55
 * @Description: SysDepartRoleDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysDepartRoleDaoImpl extends BaseServiceImpl<SysDepartRoleMapper, SysDepartRoleEntity> implements SysDepartRoleDao {
    @Override
    public List<SysDepartRoleEntity> findByRoleId(Long roleId) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDepartRoleEntity::getRoleId).eq(roleId)
        );
    }

    @Override
    public List<SysDepartRoleEntity> findByRoleIdIn(List<Long> roleIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SysDepartRoleEntity::getRoleId).in(roleIds)
        );
    }

    @Override
    public boolean deleteSysDepartRoleByDepartId(List<Long> departIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysDepartRoleEntity::getDepartId).in(departIds)
        );
    }

    @Override
    public boolean deleteSysDepartRoleByRoleId(Long roleId) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysDepartRoleEntity::getRoleId).eq(roleId)
        );
    }

    @Override
    public boolean deleteSysDepartRoleByRoleId(List<Long> roleIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysDepartRoleEntity::getRoleId).in(roleIds)
        );
    }
}

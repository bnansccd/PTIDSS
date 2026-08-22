package com.troy.system.service.impl;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.StringUtils;
import com.troy.system.dao.SysUserRoleDao;
import com.troy.system.entity.SysUserRoleEntity;
import com.troy.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色与用户的关系表 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {
    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public ResultVO insertUserRoleByUserId(Long userId, List<Long> roleIds){
        if (StringUtils.isNotEmpty(roleIds)){
            List<SysUserRoleEntity> sysUserRoleEntities = roleIds.stream().map(roleId -> {
                SysUserRoleEntity relation = new SysUserRoleEntity();
                relation.setRoleId(roleId);
                relation.setUserId(userId);
                return relation;
            }).collect(Collectors.toList());
            this.sysUserRoleDao.saveBatch(sysUserRoleEntities);
        }
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public ResultVO updateUserRoleByUserId(Long userId, List<Long> roleIds) {
        this.sysUserRoleDao.deleteByUserId(userId);
        return insertUserRoleByUserId(userId,roleIds);
    }

    @Override
    public List<SysUserRoleEntity> findByUserId(Long userId) {
        return this.sysUserRoleDao.findByUserId(userId);
    }

}

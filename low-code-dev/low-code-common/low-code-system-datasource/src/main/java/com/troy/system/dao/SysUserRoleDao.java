package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysUserRoleEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:23
 * @Description: SysUserRoleDao
 * @Version: 1.0.0
 */
public interface SysUserRoleDao extends BaseService<SysUserRoleEntity> {

    /**
     * 通过用户id查询与角色的关系
     *
     * @param userId
     * @return
     */
    List<SysUserRoleEntity> findByUserId(Long userId);

    /**
     * 通过用户id查询与角色的关系
     *
     * @param userIds
     * @return
     */
    List<SysUserRoleEntity> findByUserIdIn(List<Long> userIds);


    /**
     * @author yzy
     * @description 解除用户和角色的关系
     * @date  2022/9/5
     * @param userId
     * @return
     * @version
     */
    boolean deleteByUserId(Long userId);

    /**
     * @author yzy
     * @description 批量解除用户和角色的关系
     * @date  2022/9/5
     * @param userIds
     * @return
     * @version
     */
    boolean deleteByUserId(List<Long> userIds);

    /**
     * @author yzy
     * @description 批量解除用户和角色的关系
     * @date  2022/9/5
     * @param roleIds
     * @return
     * @version
     */
    boolean deleteByRoleId(List<Long> roleIds);
}

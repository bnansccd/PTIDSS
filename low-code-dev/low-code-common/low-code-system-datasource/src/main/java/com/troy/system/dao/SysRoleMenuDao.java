package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysRoleMenuEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:18
 * @Description: SysRoleMenuDao
 * @Version: 1.0.0
 */
public interface SysRoleMenuDao extends BaseService<SysRoleMenuEntity> {

    /**
     * 通过角色id查询角色与权限的关系
     *
     * @param roleId
     * @return
     */
    List<SysRoleMenuEntity> findByRoleId(Long roleId);

    /**
     * 通过一批角色id查询角色与权限的关系
     *
     * @param roleIds
     * @return
     */
    List<SysRoleMenuEntity> findByRoleIdIn(List<Long> roleIds);

    /**
     * @author yzy
     * @description
     * @date  2022/9/8
     * @param menuIds
     * @return
     * @version
     */
    boolean deleteByMenuId(List<Long> menuIds);

    /**
     * @author yzy
     * @description
     * @date  2022/9/8
     * @param roleId
     * @return
     * @version
     */
    boolean deleteByRoleId(Long roleId);

    /**
     * @author yzy
     * @description
     * @date  2022/9/8
     * @param roleIds
     * @return
     * @version
     */
    boolean deleteByRoleId(List<Long> roleIds);


}

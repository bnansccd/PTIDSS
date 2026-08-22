package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysDepartRoleEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/11 16:16:54
 * @Description: SysDepartRoleDao
 * @Version: 1.0.0
 */
public interface SysDepartRoleDao extends BaseService<SysDepartRoleEntity> {

    /**
     * 通过一批角色id查询角色与部门的关系
     *
     * @param roleId
     * @return
     */
    List<SysDepartRoleEntity> findByRoleId(Long roleId);


    /**
     * 通过一批角色id查询角色与部门的关系
     *
     * @param roleIds
     * @return
     */
    List<SysDepartRoleEntity> findByRoleIdIn(List<Long> roleIds);

    /**
     * @author yzy
     * @description 批量解除部门和角色的关系
     * @date  2022/9/6
     * @param departIds
     * @return
     * @version
     */
    boolean deleteSysDepartRoleByDepartId(List<Long> departIds);

    /**
     * @author yzy
     * @description 解除部门和角色的关系
     * @date  2022/9/6
     * @param roleId
     * @return
     * @version
     */
    boolean deleteSysDepartRoleByRoleId(Long roleId);

    /**
     * @author yzy
     * @description 批量解除部门和角色的关系
     * @date  2022/9/6
     * @param roleIds
     * @return
     * @version
     */
    boolean deleteSysDepartRoleByRoleId(List<Long> roleIds);
}

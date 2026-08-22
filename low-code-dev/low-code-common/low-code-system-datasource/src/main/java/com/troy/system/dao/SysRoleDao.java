package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysRoleQueryDTO;
import com.troy.system.entity.SysRoleEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:21
 * @Description: SysRoleDao
 * @Version: 1.0.0
 */
public interface SysRoleDao extends BaseService<SysRoleEntity> {

    /**
     * @param
     * @return
     * @author yzy
     * @description 角色列表（不分页）
     * @date 2022/9/7
     * @version
     */
    List<SysRoleEntity> listAll();

    /**
     * @param
     * @return
     * @author yzy
     * @description 分页
     * @date 2022/9/12
     * @version
     */
    Page<SysRoleEntity> getSysRolePage(SysRoleQueryDTO dto);


    /**
     * 通过角色编码查询角色
     *
     * @param roleCode
     * @return
     */
    SysRoleEntity findByRoleCode(String roleCode);

    /**
     * 查询最大的排序号
     *
     * @return
     */
    SysRoleEntity findMaxSort();

    /**
     * 获取指定租户默认角色
     *
     * @param tenantId
     * @param roleCode
     * @return
     */
    SysRoleEntity findByTenantIdAndRoleCode(Long tenantId, String roleCode);
}

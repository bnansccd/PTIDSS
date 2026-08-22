package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.domain.DTO.TenantMenuDTO;

import java.util.List;

/**
 * <p>
 * 角色菜单关系表 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysRoleMenuService {

    /**
     * @param roleId
     * @param menuIds
     * @return
     * @author yzy
     * @description 给角色分配菜单权限
     * @date 2022/9/22
     * @version
     */
    ResultVO insertRoleMenu(Long roleId, List<Long> menuIds);

    /**
     * 通过角色id查询角色与菜单的关系
     *
     * @return
     */
    List<Long> SysRoleMenuByRoleId(Long roleId);


    /**
     *
     * @param roleId
     * @param list
     */
    void insertAppMenu(Long roleId, List<TenantMenuDTO> list);


    /**
     * 获取
     * @param roleId
     * @return
     */
    List<Long> appMenuByRoleId(Long roleId);
}

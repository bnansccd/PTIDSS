package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.domain.DTO.SysMenuDTO;
import com.troy.system.domain.DTO.SysMenuQueryDTO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysMenuService {

    /**
     * 通过用户id查询对应的菜单
     *
     * @param userId
     * @return
     */
    List<SysMenuVO> findByUserId(Long userId);

    /**
     * 获取
     * @param userId
     * @return
     */
    List<Long> findMenuIdsByUserId(Long userId);

    /**
     * @param
     * @return
     * @author yzy
     * @description 菜单列表
     * @date 2022/9/8
     * @version
     */
    List<SysMenuVO> getSysMenuList(SysMenuQueryDTO dto);

    /**
     * 获取应用
     *
     * @return
     */
    List<SysMenuVO> findByAppIdOptional(Long appId);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增菜单
     * @date 2022/9/8
     * @version
     */
    ResultVO insertSysMenu(SysMenuDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 菜单详情
     * @date 2022/9/8
     * @version
     */
    SysMenuVO getSysMenuById(Long id);

    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑菜单
     * @date 2022/9/8
     * @version
     */
    ResultVO updateSysMenuById(Long id, SysMenuDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除菜单
     * @date 2022/9/8
     * @version
     */
    ResultVO deleteById(List<Long> ids);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 单个启用停用
     * @date 2022/9/22
     * @version
     */
    ResultVO updateSysMenuStatus(Long id);

    /**
     * 获取最新序号
     *
     * @param parentId
     * @return
     */
    Integer getCurrentSort(Long parentId);

    /**
     * 绑定APP
     *
     * @param ids
     * @param appId
     * @return
     */
    ResultVO bindApp(Long appId, Set<Long> ids);

    /**
     * 菜单树形列表
     *
     * @return
     */
    List<SysMenuVO> getSysMenuTree();


    /**
     * 获取
     * @return
     */
    List<SysMenuVO> getNewSysMenuTree();


    /**
     * 获取
     * @param appId
     * @return
     */
    List<SysMenuVO> getSysMenuTree(Long appId);

    /**
     * 通过appId查询已经配置的菜单
     *
     * @param appId
     * @return
     */
    List<SysMenuVO> findByAppId(Long appId);

    /**
     * 修改菜单展示隐藏
     *
     * @param id
     * @return
     */
    ResultVO updateSysMenuIsShow(Long id);

    /**
     * 获取租户所拥有的权限编码
     *
     * @param tenantId
     * @return
     */
    List<SysMenuVO> findByTenantId(Long tenantId);

    /**
     * 绑定租户所拥有的权限
     *
     * @param tenantId
     * @param menuCodes
     * @return
     */
    ResultVO bindTenant(Long tenantId, Set<String> menuCodes);

}

package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysMenuEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 12:12:00
 * @Description: SysMenuDao
 * @Version: 1.0.0
 */
public interface SysMenuDao extends BaseService<SysMenuEntity> {


    /**
     * @param
     * @return
     * @author yzy
     * @description 查所有最高级菜单
     * @date 2022/9/8
     * @version
     */
    List<SysMenuEntity> listAll(String menuName, Long appId);

    /**
     * @param
     * @return
     * @author yzy
     * @description 根据 父id查询
     * @date 2022/9/8
     * @version
     */
    List<SysMenuEntity> findChildrenByParentId(List<Long> parentIds);


    /**
     * 判断菜单
     *
     * @param menuCode
     * @return
     */
    SysMenuEntity findByMenuCode(String menuCode);

    /**
     * 通过appId查询可选菜单
     *
     * @param appId
     * @return
     */
    List<SysMenuEntity> findByAppIdOptional(Long appId);

    /**
     * 查询最大排序
     *
     * @param parentId
     */
    SysMenuEntity findMaxSort(Long parentId);

    /**
     * 通过AppId查询菜单
     *
     * @param appId
     * @return
     */
    List<SysMenuEntity> findByAppId(Long appId);

    /**
     * 查询应用可以绑定的菜单
     *
     * @return
     */
    List<SysMenuEntity> findByIdInOptional(Collection<Long> ids);

    /**
     * 删除菜单时
     *
     * @param ids
     */
    void removeAPPIdIn(List<Long> ids);

    /**
     * 将appId设置为空
     *
     * @param appId
     */
    void updateAppIdIsNullByAppId(Long appId);

    /**
     * 通过租户id查询菜单
     *
     * @param tenantId
     * @return
     */
    List<SysMenuEntity> findByTenantId(Long tenantId);

    /**
     * 通过一批编码查询菜单
     *
     * @param menuCodes
     * @return
     */
    List<SysMenuEntity> findByMenuCodeIn(Set<String> menuCodes);
}

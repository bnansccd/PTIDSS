package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.api.domain.VO.SysTenantVO;
import com.troy.system.domain.DTO.*;
import com.troy.system.domain.VO.TenantAppVO;
import com.troy.system.domain.VO.TenantMenuVO;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/22 10:10:24
 * @Description: 租户
 * @Version: 1.0.0
 */
public interface SysTenantService {

    /**
     * 分页查询租户
     *
     * @param dto
     * @return
     */
    PageVO<SysTenantVO> listPage(SysTenantSearchDTO dto);

    /**
     * 添加租户并初始化账号
     *
     * @param dto
     * @return
     */
    ResultVO insert(SysTenantInsertDTO dto);

    /**
     * 通过主键查询租户基础信息
     *
     * @param id
     * @return
     */
    SysTenantVO findById(Long id);

    /**
     * 修改租户
     *
     * @param id
     * @param dto
     * @return
     */
    ResultVO edit(Long id, SysTenantDTO dto);

    /**
     * 修改租户状态
     *
     * @param id
     * @return
     */
    ResultVO editStatus(Long id);

    /**
     * 删除租户
     *
     * @param ids
     * @return
     */
    ResultVO deleteByIdIn(List<Long> ids);

    /**
     * 查询所有租户
     *
     * @param dto
     * @return
     */
    List<SysTenantVO> tenantList(SysTenantSearchDTO dto);


    /**
     * 绑定app
     * @param dto
     */
    void bindTenant(TenantAppDTO dto);


    void bindTenant(Long id, TenantAppDTO dto);

    /**
     * 删除
     * @param dto
     */
    void deleteTenant(TenantAppDTO dto);


    /**
     * 获取
     * @param dto
     * @return
     */
    PageVO<TenantAppVO> findTenantAppPage(TenantAppSearchDTO dto);


    /**
     * 获取
     * @param dto
     * @return
     */
    PageVO<TenantAppVO> findCurrentTenantAppPage(TenantAppSearchDTO dto);


    /**
     * 获取
     * @param dto
     * @return
     */
    List<TenantMenuVO> getTenantMenu(TenantAppDTO dto);


    /**
     * 更新
     * @param tenantId
     * @param list
     */
    void updateTenantMenu(Long tenantId, List<TenantMenuDTO> list);



    /**
     * 获取
     * @param dto
     * @return
     */
    List<SysMenuVO> getCurrentAppMenu(TenantAppDTO dto);


}

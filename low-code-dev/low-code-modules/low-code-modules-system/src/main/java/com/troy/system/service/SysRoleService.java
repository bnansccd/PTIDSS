package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.VO.SysRoleVO;
import com.troy.system.domain.DTO.SysRoleDTO;
import com.troy.system.domain.DTO.SysRoleDataRangeDTO;
import com.troy.system.domain.DTO.SysRoleQueryDTO;

import java.util.List;

/**
 * <p>
 * 角色管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysRoleService {

    /**
     * 通过用户id查询角色信息
     *
     * @param userId
     * @return
     */
    List<SysRoleVO> findByUserId(Long userId);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 角色列表(分页)
     * @date 2022/9/7
     * @version
     */
    PageVO<SysRoleVO> getSysRoleList(SysRoleQueryDTO dto);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增角色
     * @date 2022/9/7
     * @version
     */
    ResultVO insertSysRole(SysRoleDTO dto);

    /**
     * 角色详情
     *
     * @param id
     * @return
     */
    SysRoleVO getSysRoleById(Long id);

    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑角色
     * @date 2022/9/7
     * @version
     */
    ResultVO updateSysRoleById(Long id, SysRoleDTO dto);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除角色
     * @date 2022/9/7
     * @version
     */
    ResultVO deleteSysRoleById(List<Long> ids);

    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 配置数据权限
     * @date 2022/9/7
     * @version
     */
    ResultVO updateSysRoleDataRange(Long id, SysRoleDataRangeDTO dto);


    /**
     * 获取序号
     *
     * @return
     */
    Integer getCurrentSort();

    /**
     * 通过一批用户id查询角色
     *
     * @param userIds
     * @return
     */
    List<SysRoleVO> findByUserIdIn(List<Long> userIds);

    /**
     * 通过用户Id查询自定权限的部门id
     *
     * @param userId
     * @return
     */
    List<Long> findDataRangeByUserId(Long userId);

    /**
     * 租户初始化角色
     *
     * @param tenantId
     * @param userId
     */
    Long tenantInitRole(Long tenantId, Long userId);
}

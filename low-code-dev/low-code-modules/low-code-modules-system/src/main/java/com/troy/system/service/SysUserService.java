package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.VO.AuditVO;
import com.troy.system.api.domain.VO.SysUserDetailsVO;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.domain.DTO.SysTenantInsertDTO;
import com.troy.system.domain.DTO.SysUserDTO;
import com.troy.system.domain.DTO.SysUserPageQueryDTO;

import java.util.List;

/**
 * <p>
 * 用户管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysUserService {

    /**
     * 通过用户名与租户id查询用户
     *
     * @param username
     * @return
     */
    SysUserDetailsVO sysUserByUsernameAndTenantId(String username, Long tenantId);

    /**
     * 注册帐号
     *
     * @param dto
     * @return
     */
    ResultVO sysUserRegister(RegisterDTO dto);

    /**
     * 通过用户id查询用户基本信息
     *
     * @param userId
     * @return
     */
    SysUserDetailsVO getSysUserDetail(Long userId);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 用户列表
     * @date 2022/9/2
     * @version
     */
    PageVO<SysUserDetailsVO> getSysUserList(SysUserPageQueryDTO dto);

    /**
     * @param dto
     * @return
     * @author yzy
     * @description 新增用户
     * @date 2022/9/2
     * @version
     */
    ResultVO insertSysUser(SysUserDTO dto);


    /**
     * @param id
     * @param dto
     * @return
     * @author yzy
     * @description 编辑用户
     * @date 2022/9/2
     * @version
     */
    ResultVO updateSysUserById(Long id, SysUserDTO dto);

    /**
     * @param id
     * @return
     * @author yzy
     * @description 单个用户启停用
     * @date 2022/9/2
     * @version
     */
    ResultVO updateSysUserStatus(Long id);

    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量用户启停用
     * @date 2022/9/2
     * @version
     */
    ResultVO updateSysUserStatus(String status, List<Long> ids);


    /**
     * @param ids
     * @return
     * @author yzy
     * @description 批量删除
     * @date 2022/9/2
     * @version
     */
    ResultVO deleteSysUserById(List<Long> ids);


    /**
     * 按条件获取
     *
     * @param queryParams
     * @return
     */
    List<SysUserVO> getListByCondition(String queryParams);


    /**
     * 获取用户
     * @param queryParams
     * @param tenantId
     * @return
     */
    List<SysUserVO> getListByConditionAndTenantId(String queryParams, Long tenantId);


    /**
     * 获取IDs
     * @param ids
     * @param tenantId
     * @return
     */
    List<SysUserVO> getByIds(List<Long> ids, Long tenantId);

    /**
     * 更新密码
     *
     * @param userId
     * @return
     */
    ResultVO resetPassword(Long id);

    /**
     * 更新密码
     *
     * @param userId
     * @return
     */

    void changePassword(String old, String newPassword);


    /**
     * 得到当前登录用户信息
     *
     * @return
     */
    SysUserDetailsVO current(Long appId);


    /**
     * 得到当前登录用户信息
     *
     * @return
     */
    SysUserDetailsVO current(String appCode);

    /**
     * 获取用户基础信息
     *
     * @param ids
     * @return
     */
    List<SysUserVO> findByIdIn(List<Long> ids);

    /**
     * 通过主键查询用户基础信息
     *
     * @param id
     * @return
     */
    SysUserVO findById(Long id);

    /**
     * 查询审计信息
     *
     * @param dto
     * @return
     */
    AuditVO findAuditInfo(AuditDTO dto);

    /**
     * 租户初始化账号
     *
     * @param tenantId
     * @param dto
     * @return
     */
    Long tenantInitUser(Long tenantId, SysTenantInsertDTO dto);


    /**
     * 获取ids
     * @param departIds
     * @param name
     * @return
     */
    List<SysUserVO> byDepartIdsAndUsername(List<Long> departIds, String name);

    /**
     * 获取ids
     * @param departIds
     * @param name
     * @return
     */
    List<SysUserVO> byDepartIdsAndRealName(List<Long> departIds, String name);


    /**
     * 获取
     * @return
     */
    List<SysUserVO> findByOwnerDepart();

    /**
     * 通过用户名查询
     * @param userName
     * @return
     */
    SysUserVO getByUserName(String userName);

    /**
     * 查询所有用户
     * @return
     */
    List<SysUserVO> getAll();

    /**
     * 通过用户名查询用户
     * @param names
     * @return
     */
    List<SysUserVO> getByRealNameIn(List<String> names);

    /**
     * 通过手机号查询用户
     * @param phone
     * @param tenantId
     * @return
     */
    SysUserDetailsVO sysUserByPhoneAndTenantId(String phone, Long tenantId);
}
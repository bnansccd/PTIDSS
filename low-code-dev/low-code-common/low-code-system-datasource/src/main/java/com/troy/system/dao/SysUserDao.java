package com.troy.system.dao;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.datasource.service.BaseService;
import com.troy.system.domain.DTO.SysUserPageQueryDTO;
import com.troy.system.entity.SysUserEntity;

import java.util.List;

/**
 * <p>
 * 用户管理 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysUserDao extends BaseService<SysUserEntity> {

    /**
     * 通过用户名查询用户
     *
     * @param username
     * @return
     */
    SysUserEntity findByUsername(String username);


    /**
     * 通过电话号码或者用户名查重
     *
     * @param
     * @return
     */
    SysUserEntity findByPhoneOrUserName(String phone, String userName);


    /**
     * 通过电话号码或者用户名查重
     *
     * @param
     * @return
     */
    SysUserEntity findByPhoneOrUserName(Long id, String phone, String userName);

    /**
     * @param
     * @return
     * @author yzy
     * @description 分页
     * @date 2022/9/11
     * @version
     */
    Page<SysUserEntity> getSysUserPage(SysUserPageQueryDTO dto);

    /**
     * 按条件获取
     *
     * @param queryParams
     * @return
     */
    List<SysUserEntity> getListByCondition(String queryParams);

    /**
     * 通过租户id与用户名查询用户
     *
     * @param username
     * @param tenantId
     * @return
     */
    SysUserEntity findByUsernameAndTenantId(String username, Long tenantId);


    /**
     * 获取
     * @param ids
     * @param name
     * @return
     */
    List<SysUserEntity> findByDepartIdsAndUsername(List<Long> ids, String name);


    /**
     * 获取
     * @return
     */
    List<SysUserEntity> findByOwnDepart();


    List<SysUserEntity> getByTenantId(Long tenantId);


    /**
     * 获取
     * @param ids
     * @param name
     * @return
     */
    List<SysUserEntity> findByDepartIdsAndRealName(List<Long> ids, String name);

    /**
     *
     * @param phone
     * @return
     */
    SysUserEntity findByPhone(String phone);

    /**
     * 通过用户名查询一批用户
     * @param names
     */
    List<SysUserEntity> getByRealNameIn(List<String> names);

    /**
     * 通过电话号码与租户id查询用户
     * @param phone
     * @param tenantId
     * @return
     */
    SysUserEntity sysUserByPhoneAndTenantId(String phone, Long tenantId);
}

package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.entity.SysUserRoleEntity;

import java.util.List;

/**
 * <p>
 * 角色与用户的关系表 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysUserRoleService {

    /**
     * @param
     * @return
     * @author yzy
     * @description 绑定角色
     * @date 2022/9/2
     * @version
     */
    ResultVO insertUserRoleByUserId(Long userId, List<Long> roleIds);


    /**
     * @param
     * @return
     * @author yzy
     * @description 编辑角色
     * @date 2022/9/2
     * @version
     */
    ResultVO updateUserRoleByUserId(Long userId, List<Long> roleIds);

    /**
     * @author yzy
     * @description
     * @date  2022/9/22
     * @param
     * @return
     * @version
     */
    List<SysUserRoleEntity> findByUserId(Long userId);
}

package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysPostUserEntity;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:14
 * @Description: SysPostUserDao
 * @Version: 1.0.0
 */
public interface SysPostUserDao extends BaseService<SysPostUserEntity> {

    /**
     * 通过用户id查询用户与角色的关系
     *
     * @param userId
     * @return
     */
    List<SysPostUserEntity> findByUserId(Long userId);

    /**
     * 通过用户id查询用户与角色的关系
     *
     * @param userIds
     * @return
     */
    List<SysPostUserEntity> findByUserIdIn(List<Long> userIds);


    /**
     * 通过用户解除用户与角色的关系
     *
     * @param userId
     * @return
     */
    boolean deleteByUserId(Long userId);

    /**
     * 通过多个用户id解除用户与角色的关系
     *
     * @param userIds
     * @return
     */
    boolean deleteByUserId(List<Long> userIds);

    /**
     * @author yzy
     * @description 解除多个岗位关联的用户
     * @date  2022/9/5
     * @param postIds
     * @return
     * @version
     */
    boolean deleteByPostId(List<Long> postIds);


    /**
     * 获取
     * @param userIds
     * @return
     */
    List<SysPostUserEntity> findByPostIdIn(List<Long> userIds);
}

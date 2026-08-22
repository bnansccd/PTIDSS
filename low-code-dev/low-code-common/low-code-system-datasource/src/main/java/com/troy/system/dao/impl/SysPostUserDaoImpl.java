package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysPostUserDao;
import com.troy.system.entity.SysPostUserEntity;
import com.troy.system.mapper.SysPostUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysPostUserEntityTableDef.SYS_POST_USER_ENTITY;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 13:13:15
 * @Description: SysPostUserDaoImpl
 * @Version: 1.0.0
 */
@Component
public class SysPostUserDaoImpl extends BaseServiceImpl<SysPostUserMapper, SysPostUserEntity> implements SysPostUserDao {

    @Override
    public List<SysPostUserEntity> findByUserId(Long userId) {
        return super.list(
                QueryWrapper.create()
                        .where(SysPostUserEntity::getUserId).eq(userId)
        );
    }

    @Override
    public List<SysPostUserEntity> findByUserIdIn(List<Long> userIds) {
        return super.list(
                QueryWrapper.create()
                        .where(SYS_POST_USER_ENTITY.USER_ID.in(userIds))
        );
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysPostUserEntity::getUserId).eq(userId)
        );
    }

    @Override
    public boolean deleteByUserId(List<Long> userIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SYS_POST_USER_ENTITY.USER_ID.in(userIds))
        );
    }

    @Override
    public boolean deleteByPostId(List<Long> postIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SysPostUserEntity::getPostId).in(postIds)
        );
    }

    @Override
    public List<SysPostUserEntity> findByPostIdIn(List<Long> userIds) {
        return list(
                QueryWrapper.create()
                        .where(SYS_POST_USER_ENTITY.POST_ID.in(userIds))
        );
    }
}

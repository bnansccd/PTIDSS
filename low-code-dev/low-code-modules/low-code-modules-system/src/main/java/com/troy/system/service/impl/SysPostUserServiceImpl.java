package com.troy.system.service.impl;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.StringUtils;
import com.troy.system.dao.SysPostUserDao;
import com.troy.system.entity.SysPostUserEntity;
import com.troy.system.service.SysPostUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 岗位与用户的关系表 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysPostUserServiceImpl implements SysPostUserService {

    @Autowired
    private SysPostUserDao sysPostUserDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO insertPostUserByUserId(Long userId, List<Long> postIds) {
        this.sysPostUserDao.deleteByUserId(userId);
        if (StringUtils.isNotEmpty(postIds)) {
            List<SysPostUserEntity> sysPostUserEntities = postIds.stream().map(postId -> {
                SysPostUserEntity relation = new SysPostUserEntity();
                relation.setUserId(userId);
                relation.setPostId(postId);
                return relation;
            }).collect(Collectors.toList());
            this.sysPostUserDao.saveBatch(sysPostUserEntities);
        }
        return ResultVO.success();
    }
}

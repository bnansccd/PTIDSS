package com.troy.system.service;

import com.troy.common.core.domain.ResultVO;

import java.util.List;

/**
 * <p>
 * 岗位与用户的关系表 服务类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
public interface SysPostUserService {

    /**
     * @author yzy
     * @description 绑定用户和岗位的
     * @date  2022/9/5
     * @param userId
     * @param postIds
     * @return
     * @version
     */
    ResultVO insertPostUserByUserId(Long userId, List<Long> postIds);

}

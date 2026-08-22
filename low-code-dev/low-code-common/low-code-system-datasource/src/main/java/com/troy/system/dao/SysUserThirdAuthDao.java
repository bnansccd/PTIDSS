package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.SysUserThirdAuthEntity;

import java.util.List;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2025/6/7 09:47
 * @Version: 1.0
 **/
public interface SysUserThirdAuthDao extends BaseService<SysUserThirdAuthEntity> {

    /**
     * 通过openId查询第三方用户信息
     *
     * @param openId
     * @return
     */
    SysUserThirdAuthEntity findByOpenIdAndLoginType(String openId,String loginType);

    boolean deleteByUserId(List<Long> userIds);

}

package com.troy.system.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.SysUserThirdAuthDao;
import com.troy.system.entity.SysUserThirdAuthEntity;
import com.troy.system.mapper.SysUserThirdAuthMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.troy.system.entity.table.SysUserThirdAuthEntityTableDef.SYS_USER_THIRD_AUTH_ENTITY;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2025/6/7 09:48
 * @Version: 1.0
 **/
@Component
public class SysUserThirdAuthDaoImpl extends BaseServiceImpl<SysUserThirdAuthMapper, SysUserThirdAuthEntity> implements SysUserThirdAuthDao {
    @Override
    public SysUserThirdAuthEntity findByOpenIdAndLoginType(String openId,String loginType) {
        return super.getOne(
                QueryWrapper.create()
                        .where(SYS_USER_THIRD_AUTH_ENTITY.OPEN_ID.eq(openId))
                        .and(SYS_USER_THIRD_AUTH_ENTITY.LOGIN_TYPE.eq(loginType))
        );
    }

    @Override
    public boolean deleteByUserId(List<Long> userIds) {
        return super.remove(
                QueryWrapper.create()
                        .where(SYS_USER_THIRD_AUTH_ENTITY.USER_ID.in(userIds))
        );
    }
}

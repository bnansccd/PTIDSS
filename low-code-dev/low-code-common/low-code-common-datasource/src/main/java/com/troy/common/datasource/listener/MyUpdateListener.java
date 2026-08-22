package com.troy.common.datasource.listener;

import com.mybatisflex.annotation.UpdateListener;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.config.MyBatisFlexConfiguration;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.common.datasource.strategy.DeviceRegisterFactory;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/31 10:10:50
 * @Description: MyUpdateListener
 * @Version: 1.0.0
 */
@Slf4j
public class MyUpdateListener implements UpdateListener {

    @Override
    public void onUpdate(Object o) {
        if (o instanceof BaseEntity){
            BaseEntity baseEntity = (BaseEntity) o;
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isNotNull(loginUser)){
                baseEntity.setModifyId(loginUser.getUserid());

                SysUserVO sysUserVO = loginUser.getSysUserVO();
                if (StringUtils.isNotNull(sysUserVO)){
                    baseEntity.setModifyDepartId(sysUserVO.getDepartId());
                }
            }
            baseEntity.setModifyTime(new Date());
        }
        DeviceRegisterFactory.dataEncryptionAndConsistency(o);
    }
}

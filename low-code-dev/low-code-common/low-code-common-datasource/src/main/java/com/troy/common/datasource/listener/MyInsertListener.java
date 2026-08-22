package com.troy.common.datasource.listener;

import com.mybatisflex.annotation.InsertListener;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.config.MyBatisFlexConfiguration;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.common.datasource.strategy.DeviceRegisterFactory;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/31 10:10:35
 * @Description: MyInsertListener
 * @Version: 1.0.0
 */
@Slf4j
public class MyInsertListener implements InsertListener {

    @Override
    public void onInsert(Object o) {
        if (o instanceof BaseEntity){
            BaseEntity baseEntity = (BaseEntity) o;
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isNotNull(loginUser)){
                baseEntity.setCreateId(loginUser.getUserid());
                baseEntity.setModifyId(loginUser.getUserid());
                if (StringUtils.isNotNull(loginUser.getSysUserVO())){
                    baseEntity.setCreateDepartId(loginUser.getSysUserVO().getDepartId());
                    baseEntity.setModifyDepartId(loginUser.getSysUserVO().getDepartId());
                }
            }
            baseEntity.setCreateTime(new Date());
            baseEntity.setModifyTime(new Date());
        }
        DeviceRegisterFactory.dataEncryptionAndConsistency(o);

    }
}

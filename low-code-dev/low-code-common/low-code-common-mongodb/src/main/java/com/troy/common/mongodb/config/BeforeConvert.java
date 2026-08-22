package com.troy.common.mongodb.config;

import com.troy.common.core.utils.IdWorkUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.mongodb.domain.MgBaseEntity;
import com.troy.common.security.utils.SecurityUtils;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/17 13:13:15
 * @Description: mongodb自定义审计字段
 * @Version: 1.0.0
 */
public class BeforeConvert implements BeforeConvertCallback<Object> {

    @Override
    public Object onBeforeConvert(Object o, String s) {
        Date date = new Date();
        Long userId = SecurityUtils.getUserId();
        if (o instanceof MgBaseEntity) {
            MgBaseEntity baseEntity = (MgBaseEntity) o;
            if (null == baseEntity.getId()) {
                baseEntity.setId(IdWorkUtils.getInstance().nextId());
            } else {
                baseEntity.setModifyId(userId);
                baseEntity.setModifyTime(date);
            }
            if (StringUtils.isNull(baseEntity.getCreateId())) {
                baseEntity.setCreateId(userId);
            }
            if (StringUtils.isNull(baseEntity.getCreateTime())) {
                baseEntity.setCreateTime(date);
            }
        }
        return o;
    }
}

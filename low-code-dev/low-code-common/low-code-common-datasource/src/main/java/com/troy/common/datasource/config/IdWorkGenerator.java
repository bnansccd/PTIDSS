package com.troy.common.datasource.config;

import com.mybatisflex.core.keygen.IKeyGenerator;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/12 10:10:47
 * @Description: IdWorkGenerator
 * @Version: 1.0.0
 */
public class IdWorkGenerator implements IKeyGenerator {

    private SnowFlakeIDKeyGenerator snowFlakeIDKeyGenerator;

    public IdWorkGenerator() {
        this.snowFlakeIDKeyGenerator = new SnowFlakeIDKeyGenerator();
    }

    public Long geId(){
        return snowFlakeIDKeyGenerator.nextId();
    }

  @Override
    public Object generate(Object entity, String keyColumn) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            if (StringUtils.isNotNull(baseEntity.getId())) {
                return baseEntity.getId();
            }
        }
      return snowFlakeIDKeyGenerator.nextId();
    }
}

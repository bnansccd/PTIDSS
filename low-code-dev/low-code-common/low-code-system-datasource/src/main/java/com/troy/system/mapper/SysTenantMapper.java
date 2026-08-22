package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.SysTenantEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 17:17:18
 * @Description: SysTenantMapper
 * @Version: 1.0.0
 */
@Mapper
public interface SysTenantMapper extends MyBaseMapper<SysTenantEntity> {
}

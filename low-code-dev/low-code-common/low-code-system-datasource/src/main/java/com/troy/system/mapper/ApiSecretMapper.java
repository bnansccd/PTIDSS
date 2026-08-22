package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.ApiSecretEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sym
 * @description
 * @date 2023/12/1 10:22
 */
@Mapper
public interface ApiSecretMapper extends MyBaseMapper<ApiSecretEntity> {
}

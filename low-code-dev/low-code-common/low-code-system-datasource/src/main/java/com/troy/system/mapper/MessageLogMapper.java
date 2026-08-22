package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.MessageLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageLogMapper extends MyBaseMapper<MessageLogEntity> {
}

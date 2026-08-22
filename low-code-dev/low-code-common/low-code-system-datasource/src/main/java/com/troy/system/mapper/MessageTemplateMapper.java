package com.troy.system.mapper;

import com.troy.common.datasource.mapper.MyBaseMapper;
import com.troy.system.entity.MessageTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageTemplateMapper extends MyBaseMapper<MessageTemplateEntity> {
}

package com.troy.system.dao.impl;


import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.MessageLogDao;
import com.troy.system.entity.MessageLogEntity;
import com.troy.system.mapper.MessageLogMapper;
import org.springframework.stereotype.Component;

/**
 * @author sym
 * @since 2024/5/20 14:13
 */
@Component
public class MessageLogDaoImpl extends BaseServiceImpl<MessageLogMapper, MessageLogEntity> implements MessageLogDao {

}

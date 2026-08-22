package com.troy.sync.dao.impl;

import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.sync.dao.LogDao;
import com.troy.sync.entity.LogEntity;
import com.troy.sync.mapper.LogMapper;
import org.springframework.stereotype.Service;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 10:25
 */
@Service
public class LogDaoImpl extends BaseServiceImpl<LogMapper, LogEntity> implements LogDao {
}

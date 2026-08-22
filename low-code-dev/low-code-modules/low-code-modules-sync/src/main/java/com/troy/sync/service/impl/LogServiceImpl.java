package com.troy.sync.service.impl;

import com.troy.sync.dao.LogDao;
import com.troy.sync.entity.LogEntity;
import com.troy.sync.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 10:33
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addLog(Long time, String logInfo, String param, Boolean success) {
        LogEntity log = new LogEntity();
        log.setLogInfo(logInfo);
        log.setParam(param);
        log.setExecutionTime(time);
        log.setIsSuccess(success);
        logDao.save(log);
    }


}

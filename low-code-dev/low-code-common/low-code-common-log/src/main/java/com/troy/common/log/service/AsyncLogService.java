package com.troy.common.log.service;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.system.api.RemoteLogService;
import com.troy.system.api.domain.VO.SysOperLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 11:11:52
 * @Description: 异步调用日志服务
 * @Version: 1.0.0
 */
@Service
public class AsyncLogService {
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) {
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}

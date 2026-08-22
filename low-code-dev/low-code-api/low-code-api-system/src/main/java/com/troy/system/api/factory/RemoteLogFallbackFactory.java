package com.troy.system.api.factory;

import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.RemoteLogService;
import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.api.domain.VO.SysOperLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 16:16:01
 * @Description: RemoteLogFallbackFactory
 * @Version: 1.0.0
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable) {
        LOGGER.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService() {
            @Override
            public ResultVO<Boolean> saveLog(SysOperLog sysOperLog, String source) {
                return ResultVO.fail();
            }

            @Override
            public ResultVO<Boolean> saveLogininfor(SysLogininforDTO sysLogininforDTO, String source) {
                return ResultVO.fail();
            }
        };

    }
}

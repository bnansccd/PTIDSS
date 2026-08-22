package com.troy.system.api;

import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.api.domain.VO.SysOperLog;
import com.troy.system.api.factory.RemoteLogFallbackFactory;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 11:11:56
 * @Description: 日志服务
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteLogService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService {
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @param source     请求来源
     * @return 结果
     */
    @PostMapping("/operlog")
    ResultVO<Boolean> saveLog(@RequestBody SysOperLog sysOperLog, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 保存访问记录
     *
     * @param sysLogininforDTO 访问实体
     * @param source           请求来源
     * @return 结果
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "logininfor")
    ResultVO<Boolean> saveLogininfor(@RequestBody SysLogininforDTO sysLogininforDTO, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}

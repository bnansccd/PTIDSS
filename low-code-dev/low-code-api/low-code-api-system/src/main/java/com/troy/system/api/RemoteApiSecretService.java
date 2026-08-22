package com.troy.system.api;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.DTO.ApiSecretEntity;
import com.troy.system.api.factory.RemoteApiSecretFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:26
 * @Description: RemoteUserService
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteApiSecretService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteApiSecretFallbackFactory.class)
public interface RemoteApiSecretService {


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "apiSecret/orgId")
    ResultVO<ApiSecretEntity> code(@RequestParam("orgId") String orgId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}

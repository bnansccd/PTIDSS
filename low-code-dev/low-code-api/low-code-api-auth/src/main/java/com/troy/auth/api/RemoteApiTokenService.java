package com.troy.auth.api;

import com.troy.auth.api.doamin.DTO.InnerLoginDTO;
import com.troy.auth.api.doamin.VO.LoginInfoVO;
import com.troy.auth.api.factory.RemoteApiTokenFallbackFactory;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2026/3/31 11:36
 * @Version: 1.0
 **/
@FeignClient(contextId = "remoteApiTokenService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.AUTH_SERVICE, fallbackFactory = RemoteApiTokenFallbackFactory.class)
public interface RemoteApiTokenService {

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "login")
    ResultVO<LoginInfoVO> login(@RequestBody InnerLoginDTO dto, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}


package com.troy.system.api;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.factory.RemoteSsoFallbackFactory;
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
@FeignClient(contextId = "remoteSsoService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSsoFallbackFactory.class)
public interface RemoteSsoService {


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sso/code")
    ResultVO<String> code(@RequestParam("code") String code, @RequestHeader(SecurityConstants.FROM_SOURCE) String source, @RequestParam ("domain") String domain);
}

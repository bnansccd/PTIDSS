package com.troy.system.api;

import com.troy.system.api.domain.VO.SysDomainNameVO;
import com.troy.system.api.factory.RemoteSysDomainNameFallbackFactory;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 16:16:33
 * @Description: RemoteSysDomainNameService
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteSysDomainNameService", path = UrlConstants.RPC_RESTFUL, name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysDomainNameFallbackFactory.class)
public interface RemoteSysDomainNameService {

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "domainName")
    ResultVO<SysDomainNameVO> findByDomainNameOrUniversalDomainName(@RequestParam(value = "domainName") String domainName, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}

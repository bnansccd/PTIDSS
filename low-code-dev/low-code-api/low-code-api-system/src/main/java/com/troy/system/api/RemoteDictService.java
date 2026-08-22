package com.troy.system.api;

import com.troy.system.api.domain.VO.SysDictVO;
import com.troy.system.api.factory.RemoteDictFallbackFactory;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:26
 * @Description: RemoteUserService
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteDictService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictFallbackFactory.class)
public interface RemoteDictService {


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDict/parentType/{parentType}")
    ResultVO<List<SysDictVO>> getSysDictByParentType(@PathVariable(value = "parentType") String parentType, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}

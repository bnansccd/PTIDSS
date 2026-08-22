package com.troy.system.api;


import com.troy.system.api.factory.RemoteSysPostFallbackFactory;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "remoteSysPostService",path = UrlConstants.RPC_RESTFUL,name = ServiceNameConstants.SYSTEM_SERVICE,fallbackFactory = RemoteSysPostFallbackFactory.class)
public interface RemoteSysPostService {

}

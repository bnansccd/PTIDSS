package com.troy.system.api;

import com.troy.system.api.factory.RemoteSysAppFallbackFactory;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @author chenxl
 */
@FeignClient(contextId = "remoteSysAppService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysAppFallbackFactory.class)
public interface RemoteSysAppService {

}

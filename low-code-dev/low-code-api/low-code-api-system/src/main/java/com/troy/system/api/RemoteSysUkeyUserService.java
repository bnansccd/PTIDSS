package com.troy.system.api;

import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.DTO.UkeyUserDTO;
import com.troy.system.api.domain.VO.UkeyUserVO;
import com.troy.system.api.factory.RemoteSysUkeyUserFallbackFactory;
import com.troy.system.api.factory.RemoteSysUserFallbackFactory;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author sym
 * @since 2024/11/14 16:24
 */
@FeignClient(contextId = "remoteSysUkeyUserService", path = UrlConstants.RPC_RESTFUL+"ukey/", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysUkeyUserFallbackFactory.class)
public interface RemoteSysUkeyUserService {

    @ApiModelProperty("获取绑定关系")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "getList")
    ResultVO<List<UkeyUserVO>> getList(@RequestBody UkeyUserDTO dto);
}

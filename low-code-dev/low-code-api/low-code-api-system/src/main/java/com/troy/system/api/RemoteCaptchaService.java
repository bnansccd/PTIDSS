package com.troy.system.api;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.api.domain.DTO.OverrunMsgDTO;
import com.troy.system.api.factory.RemoteCaptchaFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 14:14:26
 * @Description: RemoteUserService
 * @Version: 1.0.0
 */
@FeignClient(contextId = "remoteCaptchaService", path = UrlConstants.RPC_RESTFUL, value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCaptchaFactory.class)
public interface RemoteCaptchaService {

    /**
     * 发送短信
     * @param phone
     * @param key
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "captcha/{key}/{phone}")
    ResultVO sendMsg(@PathVariable("phone") String phone, @PathVariable("key") Long key, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);




    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"captcha/overrun/msg")
    ResultVO sendOverrunMsg(@RequestBody List<OverrunMsgDTO> dtos, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}

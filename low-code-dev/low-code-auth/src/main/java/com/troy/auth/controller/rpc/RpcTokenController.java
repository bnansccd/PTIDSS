package com.troy.auth.controller.rpc;

import com.troy.auth.api.doamin.DTO.InnerLoginDTO;
import com.troy.auth.api.doamin.VO.LoginInfoVO;
import com.troy.auth.api.doamin.VO.LoginTokenVO;
import com.troy.auth.service.SysLoginService;
import com.troy.common.core.constant.CacheConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.redis.service.RedisService;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.common.security.service.TokenService;
import com.troy.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2026/3/31 11:44
 * @Version: 1.0
 **/
@Tag(name = "RPC-内部登录信息控制")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RpcTokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private RedisService redisService;

    @InnerAuth
    @Operation(summary = "内部登录")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "login")
    public ResultVO<LoginInfoVO> login(@Validated @RequestBody InnerLoginDTO dto) {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(dto.getUsername(),dto.getPhone(), dto.getPassword(), dto.getDomainName(), dto.isCheck());
        // 获取登录token
        LoginTokenVO loginTokenVO = tokenService.createToken(userInfo, dto.getAppId());
        redisService.deleteObject(CacheConstants.LOGIN_LOCK_KEY+dto.getUsername());
        LoginInfoVO loginInfoVO = new LoginInfoVO();
        loginInfoVO.setLoginTokenVO(loginTokenVO);
        loginInfoVO.setUserid(userInfo.getUserid());
        loginInfoVO.setUsername(userInfo.getUsername());
        redisService.deleteObject(CacheConstants.LOGIN_LOCK_KEY+userInfo.getUsername());
        return ResultVO.success(loginInfoVO);
    }

}

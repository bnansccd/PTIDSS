package com.troy.auth.controller.web;

import com.troy.auth.api.doamin.VO.LoginTokenVO;
import com.troy.common.core.domain.DTO.LoginDTO;
import com.troy.auth.service.SysLoginService;
import com.troy.common.core.constant.CacheConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.JwtUtils;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.common.security.auth.AuthUtil;
import com.troy.common.security.service.TokenService;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/2 17:17:28
 * @Description: token 控制
 * @Version: 1.0.0
 */
@Api(tags = "登录信息控制")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private RedisService redisService;


    @ApiOperation(value = "登录")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "login")
    public ResultVO<?> login(HttpServletRequest request, @Validated @RequestBody LoginDTO dto) {
        String domainName = ServletUtils.getDomainName(request);
        if (StringUtils.isBlank(domainName)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }
        // 用户登录
        LoginUser userInfo = sysLoginService.login(dto.getUsername(),null, dto.getPassword(), domainName, Boolean.TRUE);
        // 获取登录token
        LoginTokenVO loginTokenVO = tokenService.createToken(userInfo, dto.getAppId());
        redisService.deleteObject(CacheConstants.LOGIN_LOCK_KEY+dto.getUsername());
        return ResultVO.success(loginTokenVO);
    }

    @ApiOperation(value = "登录")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "loginN")
    public ResultVO<?> loginN(HttpServletRequest request, @Validated @RequestBody LoginDTO dto) {
        String domainName = ServletUtils.getDomainName(request);
        if (StringUtils.isBlank(domainName)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }

        if (StringUtils.isNotEmpty(dto.getRequestId())){
            throw new ServiceException(ResultEnum.MSG_OUT_OF_DATE);
        }

        Object cacheObject = redisService.getCacheObject(BaseRedisConstants.REQUEST_ID + dto.getRequestId());
        if (cacheObject == null || !cacheObject.toString().equals(StringUtils.lowerCase(dto.getCapCode()))){
            throw new ServiceException(ResultEnum.BE_CURRENT, "图形验证失败或者图形验证码已过期！");
        }


        // 用户登录
        LoginUser userInfo = sysLoginService.login(dto.getUsername(),null, dto.getPassword(), domainName, Boolean.TRUE);
        // 获取登录token
        LoginTokenVO loginTokenVO = tokenService.createToken(userInfo, dto.getAppId());
        redisService.deleteObject(CacheConstants.LOGIN_LOCK_KEY+dto.getUsername());
        return ResultVO.success(loginTokenVO);
    }

    @ApiOperation(value = "登录")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "loginWithCode")
    public ResultVO<?> loginWithCode(HttpServletRequest request, @Validated @RequestBody LoginDTO dto) {
        String domainName = ServletUtils.getDomainName(request);
        if (StringUtils.isBlank(domainName)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }


        Long object = redisService.getCacheObject(BaseRedisConstants.DOMAIN + domainName);
        String code = redisService.getCacheObject(BaseRedisConstants.MSG_TIME + object +":"+ dto.getUsername());
        if (StringUtils.equals(dto.getCode(), code)){

            redisService.deleteObject(BaseRedisConstants.MSG_TIME + object +":"+ dto.getUsername());
            // 用户登录
            LoginUser userInfo = sysLoginService.login(dto.getUsername(),null, dto.getPassword(), domainName, Boolean.FALSE);
            LoginTokenVO loginTokenVO = tokenService.createToken(userInfo, dto.getAppId());
            return ResultVO.success(loginTokenVO);
        }

        throw new ServiceException(ResultEnum.EXPIRE, "验证码");
    }

    @ApiOperation(value = "刷新用户登录信息")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "refresh/userInfo")
    public ResultVO refreshUserInfo() {
        // 刷新用户登录信息
        return sysLoginService.refreshUserInfo();
    }

    @ApiOperation(value = "退出")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "logout")
    public ResultVO<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            Long tenantId = JwtUtils.getTenantId(token);
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username, tenantId);
        }
        return ResultVO.success();
    }

    @ApiOperation(value = "刷新token")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "refresh")
    public ResultVO<?> refresh(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return ResultVO.success();
        }
        return ResultVO.success();
    }

    @ApiOperation(value = "注册")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "register")
    public ResultVO<?> register(@RequestBody @Validated RegisterDTO dto) {
        // 用户注册
        sysLoginService.register(dto);
        return ResultVO.success();
    }
}

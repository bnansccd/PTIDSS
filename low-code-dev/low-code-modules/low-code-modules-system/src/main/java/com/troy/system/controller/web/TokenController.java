package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.oauth2.annotation.OauthToken;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.system.domain.VO.AccessToken;
import com.troy.system.entity.ApiSecretEntity;
import com.troy.system.service.ApiSecretService;
import com.troy.system.util.JWTUtil;
import com.troy.system.util.MD5Util;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sym
 * @description
 * @date 2023/11/30 14:37
 */
@Api(tags = "第三方令牌获取")
@Slf4j
@RestController
@RequestMapping(UrlConstants.THIRD_RESTFUL)
public class TokenController {

    @Autowired
    private ApiSecretService apiSecretService;

    @Autowired
    private RedisService redisService;

    /**
     * API Token
     *
     * @param
     * @return
     */
    @PostMapping("token")
    @OauthToken
    public ResultVO apiToken(@RequestHeader Map<String, String> httpHeaders) {
        String timeStamp = httpHeaders.get("timestamp");
        String sign = httpHeaders.get("sign");
        String orgId = httpHeaders.get("orgid");
        if (StringUtils.isNull(orgId) || StringUtils.isNull(timeStamp) || StringUtils.isNull(sign)) {
            throw new ServiceException(ResultEnum.ERROR, "参数");
        }
        long reqeustInterval = System.currentTimeMillis() - Long.valueOf(timeStamp);
        if (reqeustInterval > 5 * 60 * 1000) {
            throw new ServiceException(ResultEnum.EXPIRE, "timestamp");
        }
        // 1\. 根据orgId查询数据库获取orgKey
        ApiSecretEntity oneByOrgId = redisService.getCacheObject(BaseRedisConstants.ORG_INFO + orgId);
        if (StringUtils.isNull(oneByOrgId)) {
            oneByOrgId = apiSecretService.getOneByOrgId(orgId);
            if (oneByOrgId != null) {
                List<ApiSecretEntity> all = apiSecretService.getAll();
                all.forEach(e -> {
                    redisService.setCacheObject(BaseRedisConstants.ORG_INFO + e.getOrgId(), e);
                });
            } else {
                redisService.setCacheObject(BaseRedisConstants.ORG_INFO + orgId, new ApiSecretEntity(),5L, TimeUnit.MINUTES);
            }
        }
        if (oneByOrgId == null || StringUtils.isNull(oneByOrgId.getOrgId())) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "组织id");
        }
        // 2\. 校验签名
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("orgId", orgId);
        hashMap.put("orgKey", oneByOrgId.getOrgKey());
        String signature = MD5Util.mapEncry(hashMap);
        System.out.println(hashMap);
        log.info(signature);
        if (!signature.equals(sign)) {
            throw new ServiceException(ResultEnum.ERROR, "签名");
        }
        AccessToken token = getToken(hashMap);
        return ResultVO.success(ResultEnum.SUCCESS, token);
    }

    private AccessToken getToken(HashMap<String, Object> hashMap) {
        AccessToken accessToken = new AccessToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 7200);
        Date expireTime = calendar.getTime();
        try {
            String jwt = JWTUtil.createJWT(hashMap);
            accessToken.setToken(jwt);
            accessToken.setExpireTime(expireTime);
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.OPERATE_FAIL, "生成token");
        }
        return accessToken;
    }

}

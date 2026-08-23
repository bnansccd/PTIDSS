package com.troy.common.oauth2.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.oauth2.util.JWTUtil;
import com.troy.common.oauth2.util.MD5Util;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.RemoteApiSecretService;
import com.troy.system.api.domain.DTO.ApiSecretEntity;
import org.apache.commons.lang3.StringEscapeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 11:11:44
 * @Description: 操作日志记录处理
 * @Version: 1.0.0
 */
@Aspect
@Component
public class OauthAspect {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteApiSecretService remoteApiSecretService;

    @Before("@annotation(com.troy.common.oauth2.annotation.OauthToken)")
    public void oauthToken(JoinPoint joinPoint) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String orgid = request.getHeader("orgid");
            String timestamp = request.getHeader("timestamp");
            String sign = request.getHeader("sign");
            if (StringUtils.isEmpty(orgid) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(sign)) {
                throw new ServiceException(ResultEnum.ERROR, "参数");
            }
        }
    }

    @Before("@annotation(com.troy.common.oauth2.annotation.OauthApi)")
    public void oauthApi(JoinPoint joinPoint) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 获取Body内容
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            String token = request.getHeader("token");
            String timestamp = request.getHeader("timestamp");
            String sign = request.getHeader("sign");
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(sign) || StringUtils.isEmpty(body)) {
                throw new ServiceException(ResultEnum.ERROR, "参数");
            }

            long currentTime = System.currentTimeMillis();
            long requestTime;

            try {
                requestTime = Long.parseLong(timestamp);
            } catch (NumberFormatException e) {
                throw new ServiceException(ResultEnum.ERROR, "timestamp格式错误");
            }

            // 判断时间差绝对值是否大于 5 分钟（300000 毫秒）
            if (Math.abs(currentTime - requestTime) > 5 * 60 * 1000) {
                throw new ServiceException(ResultEnum.ERROR, "请求已过期");
            }

            // 3\. 校验Token是否存在
            try {
                DecodedJWT decodedJWT = JWTUtil.parseJWT(token);
                String orgId = decodedJWT.getHeaderClaim("orgId").asString();
                //获取 token时已经放入 redis，此时只需要取
                JSONObject oneByOrgId = redisService.getCacheObject(BaseRedisConstants.ORG_INFO + orgId);
                if(StringUtils.isNull(oneByOrgId)){
                    ResultVO<ApiSecretEntity> code = remoteApiSecretService.code(orgId, SecurityConstants.INNER);
                    if (ResultVO.isSuccess(code)){
                        oneByOrgId = JSONObject.parseObject(JSONObject.toJSONString(code.getData()));
                    }
                }
                //为null直接 catch
                SecurityContextHolder.set("orgId", oneByOrgId.get("orgId"));
                //租户赋值
                SecurityContextHolder.setTenantId(oneByOrgId.getLong("tenantId"));
            } catch (Exception e) {
                throw new ServiceException(ResultEnum.EXPIRE, "token验证异常或token");
            }

            String string = StringEscapeUtils.unescapeHtml4(body.toString());

            Map<String, Object> parameterMap = new HashMap<>();
            String str = string.replaceAll("\\s*", "");
            parameterMap.put("token", token);
            parameterMap.put("timeStamp", timestamp);
            parameterMap.put("json", str);
            String signature = MD5Util.mapEncry(parameterMap);
            System.out.println(parameterMap);
            boolean flag = signature.equals(sign);
            if (!flag) {
                throw new ServiceException(ResultEnum.ERROR, "签名");
            }
        }
    }

}

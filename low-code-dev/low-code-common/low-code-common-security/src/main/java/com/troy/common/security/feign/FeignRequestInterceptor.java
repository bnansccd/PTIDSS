package com.troy.common.security.feign;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.ip.IpUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:40
 * @Description: feign 请求拦截器
 * @Version: 1.0.0
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest)) {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(SecurityConstants.DETAILS_USER_ID);
            if (StringUtils.isNotEmpty(userId)) {
                requestTemplate.header(SecurityConstants.DETAILS_USER_ID, userId);
            }
            String userName = headers.get(SecurityConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName)) {
                requestTemplate.header(SecurityConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
            }
            String tenantId = headers.get(SecurityConstants.TENANT_ID);
            if (StringUtils.isNotEmpty(tenantId)) {
                requestTemplate.header(SecurityConstants.TENANT_ID, tenantId);
            }
            String DEPART_NAME = headers.get(SecurityConstants.DEPART_NAME);
            if (StringUtils.isNotEmpty(DEPART_NAME)) {
                requestTemplate.header(SecurityConstants.DEPART_NAME, DEPART_NAME);
            }
            String DEPART_ID = headers.get(SecurityConstants.DEPART_ID);
            if (StringUtils.isNotEmpty(DEPART_ID)) {
                requestTemplate.header(SecurityConstants.DEPART_ID, DEPART_ID);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr(ServletUtils.getRequest()));
        }
    }
}

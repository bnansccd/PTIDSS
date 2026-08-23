package com.troy.common.security.interceptor;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.auth.AuthUtil;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.model.LoginUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:47
 * @Description: 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 * @Version: 1.0.0
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));
        SecurityContextHolder.setDepartId(ServletUtils.getHeader(request, SecurityConstants.DEPART_ID));
        SecurityContextHolder.setDepartName(ServletUtils.getHeader(request, SecurityConstants.DEPART_NAME));

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                SecurityContextHolder.setTenantId(loginUser.getTenantId());
                if (StringUtils.isNotNull(loginUser.getSysDepartVO())){
                    SecurityContextHolder.setDepartName(loginUser.getSysDepartVO().getDepartName());
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}

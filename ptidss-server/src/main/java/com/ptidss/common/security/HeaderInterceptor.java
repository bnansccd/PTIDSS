package com.ptidss.common.security;

import com.ptidss.common.constant.Constants;
import com.ptidss.common.utils.StrUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求头拦截器（对齐 low-code-dev HeaderInterceptor）：
 * 1) 解析 Authorization → 加载 LoginUser → 写入会话区域（X-Region-Code，多省路由）；
 * 2) 未携带令牌的请求放行，由 @RequiresPermissions/@RequiresRoles 或业务层按需鉴权
 *    （登录/验证码等公开接口不校验）；
 * 3) 校验会话区域属于该用户授权区域（评审决议⑤：角色 × 区域双重授权）。
 */
@Component
public class HeaderInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final SecurityUtils securityUtils;

    public HeaderInterceptor(TokenService tokenService, SecurityUtils securityUtils) {
        this.tokenService = tokenService;
        this.securityUtils = securityUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LoginUser user = tokenService.getLoginUser(request);
        if (user != null) {
            // V2.4 滑动续期：剩余不足阈值时 TokenService 已重签 JWT，经响应头回传前端替换本地令牌
            if (StrUtils.isNotBlank(user.getNewToken())) {
                response.setHeader(Constants.NEW_TOKEN_HEADER, user.getNewToken());
            }
            // 会话区域：请求头优先，其次取用户默认区域（首个授权区域）
            String region = request.getHeader(Constants.REGION_HEADER);
            if (StrUtils.isBlank(region) && user.getRegions() != null && !user.getRegions().isEmpty()) {
                region = user.getRegions().iterator().next();
            }
            if (StrUtils.isNotBlank(region)) {
                // 非管理员必须已授权该区域
                if (!user.isAdmin()) {
                    securityUtils.checkRegionAccess(region);
                }
                user.setRegionCode(region);
            }
        }
        return true;
    }
}

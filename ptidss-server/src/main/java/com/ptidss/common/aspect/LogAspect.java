package com.ptidss.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.ptidss.common.annotation.Log;
import com.ptidss.common.security.LoginUser;
import com.ptidss.common.security.TokenService;
import com.ptidss.common.utils.IdUtils;
import com.ptidss.common.utils.ServletUtils;
import com.ptidss.common.utils.SnowflakeIdGenerator;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.AuditLog;
import com.ptidss.system.mapper.AuditLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 审计日志切面（对齐 low-code-dev LogAspect + AsyncLogService）：
 * 注解方法执行后异步落库 audit_log（等保三级审计：操作人/IP/UA/入参/结果/区域/快照）
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final TokenService tokenService;
    private final AuditLogMapper auditLogMapper;

    public LogAspect(TokenService tokenService, AuditLogMapper auditLogMapper) {
        this.tokenService = tokenService;
        this.auditLogMapper = auditLogMapper;
    }

    @Before("@annotation(anno)")
    public void doBefore(JoinPoint point, Log anno) {
        // 入参快照由 after 阶段统一记录（含异常场景）
    }

    @AfterReturning(pointcut = "@annotation(anno)", returning = "result")
    public void doAfterReturning(JoinPoint point, Log anno, Object result) {
        saveAudit(point, anno, "success", result);
    }

    @AfterThrowing(pointcut = "@annotation(anno)", throwing = "e")
    public void doAfterThrowing(JoinPoint point, Log anno, Exception e) {
        saveAudit(point, anno, "fail", e.getMessage());
    }

    @Async
    protected void saveAudit(JoinPoint point, Log anno, String result, Object payload) {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            LoginUser user = tokenService.getLoginUser(request);
            AuditLog audit = new AuditLog();
            audit.setId(SnowflakeIdGenerator.nextId());
            audit.setTraceId(IdUtils.traceId());
            audit.setUserId(user == null ? 0L : user.getUserid());
            audit.setUsername(resolveUsername(user, point.getArgs()));
            audit.setAction(anno.action());
            audit.setTargetType(StrUtils.isNotBlank(anno.targetType()) ? anno.targetType() : point.getSignature().getDeclaringTypeName());
            audit.setTargetId(resolveTargetId(point.getArgs()));
            audit.setIp(ServletUtils.getClientIp(request));
            audit.setUserAgent(request == null ? "" : truncate(request.getHeader("User-Agent"), 255));
            audit.setResult(result);
            audit.setRegionCode(user == null ? null : user.getRegionCode());
            audit.setCreatedAt(new Date());
            if (anno.recordArgs()) {
                String json = truncate(JSON.toJSONString(point.getArgs()), 4000);
                if ("success".equals(result)) {
                    audit.setAfterSnapshot(json);
                } else {
                    audit.setBeforeSnapshot(json);
                }
            }
            auditLogMapper.insert(audit);
        } catch (Exception e) {
            log.warn("审计日志写入失败：{}", e.getMessage());
        }
    }

    /** 操作人用户名：已登录取会话；未登录（如 login）尝试从第一个入参 DTO 提取 getUsername() */
    private String resolveUsername(LoginUser user, Object[] args) {
        if (user != null && user.getUsername() != null) {
            return user.getUsername();
        }
        if (args != null && args.length > 0 && args[0] != null) {
            try {
                java.lang.reflect.Method getter = args[0].getClass().getMethod("getUsername");
                Object value = getter.invoke(args[0]);
                if (value != null) {
                    return String.valueOf(value);
                }
            } catch (Exception ignored) {
                // 非标准 DTO，忽略
            }
        }
        return null;
    }

    /** 尝试从第一个参数提取业务主键（目标对象 toString 或 Long） */
    private String resolveTargetId(Object[] args) {
        if (args == null || args.length == 0) {
            return "-";
        }
        Object first = args[0];
        if (first == null) {
            return "-";
        }
        if (first instanceof Number) {
            return String.valueOf(first);
        }
        String s = first.toString();
        return truncate(s, 64);
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() > max ? s.substring(0, max) : s;
    }
}

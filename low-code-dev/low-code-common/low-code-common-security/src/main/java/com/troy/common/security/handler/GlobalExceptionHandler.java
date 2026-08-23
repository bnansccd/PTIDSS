package com.troy.common.security.handler;

import com.troy.common.core.constant.HttpStatus;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.exception.DemoModeException;
import com.troy.common.core.exception.InnerAuthException;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.exception.auth.NotPermissionException;
import com.troy.common.core.exception.auth.NotRoleException;
import com.troy.common.core.exception.file.FileException;
import com.troy.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:42
 * @Description: 全局异常处理器
 * @Version: 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResultVO handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return ResultVO.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResultVO handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return ResultVO.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVO handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                        HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String messages = StringUtils.format("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        log.error(messages);
        return ResultVO.fail(messages);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ResultVO handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? ResultVO.fail(code, e.getMessage()) : ResultVO.fail(e.getMessage());
    }

    /**
     * 文件处理异常
     */
    @ExceptionHandler(FileException.class)
    public ResultVO handleServiceException(FileException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        return ResultVO.fail(e.getDefaultMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVO handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return ResultVO.fail("使用人数过多！");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResultVO handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ResultVO.fail("使用人数过多！");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ResultVO handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResultVO.fail(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResultVO.fail(message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResultVO messageExceptionHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResultVO.fail("参数不符合规范异常！");
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public ResultVO handleInnerAuthException(InnerAuthException e) {
        log.error(e.getMessage(), e);
        return ResultVO.fail(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public ResultVO handleDemoModeException(DemoModeException e) {
        return ResultVO.fail("演示模式，不允许操作");
    }
}

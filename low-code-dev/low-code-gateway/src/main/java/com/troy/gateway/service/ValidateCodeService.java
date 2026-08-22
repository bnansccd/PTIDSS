package com.troy.gateway.service;

import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.exception.CaptchaException;

import java.io.IOException;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 17:17:39
 * @Description: 验证码处理
 * @Version: 1.0.0
 */
public interface ValidateCodeService {
    /**
     * 生成验证码
     */
    public ResultVO createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}

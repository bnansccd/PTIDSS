package com.ptidss.auth.service;

import com.google.code.kaptcha.Producer;
import com.ptidss.auth.dto.CaptchaResult;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.IdUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 验证码服务（对齐 low-code-dev CaptchaController）：
 * 生成图片 Base64 + 键；登录时按键校验（5 分钟有效，一次性消费）
 */
@Service
public class CaptchaService {

    private final Producer captchaProducer;

    private final Cache<String, String> captchaCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public CaptchaService(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    public CaptchaResult generate() {
        String key = IdUtils.fastUUID();
        String code = captchaProducer.createText();
        captchaCache.put(key, code);

        BufferedImage image = captchaProducer.createImage(code);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            CaptchaResult result = new CaptchaResult();
            result.setCaptchaKey(key);
            result.setImage("data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray()));
            return result;
        } catch (Exception e) {
            throw new ServiceException("验证码生成失败");
        }
    }

    /** 校验验证码（不区分大小写，一次性消费） */
    public void verify(String key, String code) {
        if (key == null || code == null) {
            throw new ServiceException("请输入验证码");
        }
        String cached = captchaCache.getIfPresent(key);
        if (cached == null) {
            throw new ServiceException("验证码已过期，请刷新重试");
        }
        captchaCache.invalidate(key);
        if (!cached.equalsIgnoreCase(code.trim())) {
            throw new ServiceException("验证码错误");
        }
    }
}

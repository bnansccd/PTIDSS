package com.troy.gateway.service.impl;

import com.google.code.kaptcha.Producer;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.CaptchaException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.sign.Base64;
import com.troy.common.core.utils.uuid.IdUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.gateway.config.properties.CaptchaProperties;
import com.troy.gateway.service.ValidateCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 17:17:40
 * @Description: 验证码实现处理
 * @Version: 1.0.0
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    public static final Logger LOGGER= LoggerFactory.getLogger(ValidateCodeServiceImpl.class);

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 生成验证码
     */
    @Override
    public ResultVO createCaptcha() throws IOException, CaptchaException {
        ResultVO resultVo = ResultVO.success();
        boolean captchaEnabled = captchaProperties.getEnabled();
        Map<String, Object> ajax = new HashMap();
        resultVo.setData(ajax);
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return resultVo;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        String captchaType = captchaProperties.getType();
        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        LOGGER.info("验证码是：{}",code);
        redisService.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return ResultVO.fail(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return resultVo;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) throws CaptchaException {
        if (StringUtils.isEmpty(code)) {
            throw new CaptchaException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.AUTH_CODE));
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException(ResultEnum.getMsg(ResultEnum.EXPIRE,ResultConstants.AUTH_CODE));
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisService.getCacheObject(verifyKey);
        redisService.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException(ResultEnum.getMsg(ResultEnum.ERROR,ResultConstants.AUTH_CODE));
        }
    }
}

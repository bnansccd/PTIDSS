package com.troy.auth.controller.web;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.uuid.UUID;
import com.troy.common.redis.constants.BaseRedisConstants;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.RemoteCaptchaService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class CaptchaController {


    @Autowired
    private RemoteCaptchaService remoteCaptchaService;

    @Autowired
    private RedisService redisService;

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 +"captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        specCaptcha.setFont(Captcha.FONT_10);
        // 设置字体
//        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        specCaptcha.setLen(4);

        // 输出图片流
        String string = UUID.fastUUID().toString();
        redisService.setCacheObject(BaseRedisConstants.REQUEST_ID+string, specCaptcha.text().toLowerCase(), 1L, TimeUnit.MINUTES);
        response.setHeader("requestId", string);
        response.setHeader("Access-Control-Expose-Headers", "*");
        specCaptcha.out(response.getOutputStream());
    }

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 +"captcha/msg")
    public ResultVO captcha(@RequestParam String phone, @RequestParam String requestId, @RequestParam String code, HttpServletRequest request) throws Exception {

        String domainName = ServletUtils.getDomainName(request);
        if (StringUtils.isBlank(domainName)) {
            throw new ServiceException(ResultEnum.ILLEGAL_LINK);
        }

        Object cacheObject = redisService.getCacheObject(BaseRedisConstants.REQUEST_ID + requestId);
        if (cacheObject == null || !cacheObject.toString().equals(StringUtils.lowerCase(code))){
            throw new ServiceException(ResultEnum.BE_CURRENT, "图形验证失败或者图形验证码已过期！");
        }

        Long object = redisService.getCacheObject(BaseRedisConstants.DOMAIN + domainName);
        return remoteCaptchaService.sendMsg(phone, object, SecurityConstants.INNER);
    }

}

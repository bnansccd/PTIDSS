package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.DTO.NormalMsgDTO;
import com.troy.system.api.domain.DTO.OverrunMsgDTO;
import com.troy.system.service.SendService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "RPC-调用短信发送")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCCaptchaController {

    @Autowired
    private SendService sendService;

    @InnerAuth
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "captcha/{key}/{phone}")
    public ResultVO sendMsg(@PathVariable String phone, @PathVariable Long key) {
        sendService.sendMsg(phone, key);
        return ResultVO.success();
    }


    @InnerAuth
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"captcha/overrun/msg")
    public ResultVO sendOverrunMsg(@RequestBody List<OverrunMsgDTO> dtos){
        if(StringUtils.isNotEmpty(dtos)){
            sendService.sendOverrunMsg(dtos);
        }
        return ResultVO.success();
    }

    @InnerAuth
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"captcha/normal/msg")
    public ResultVO sendNormalMsg(@RequestBody NormalMsgDTO dto){
        sendService.sendNormalMsg(dto.getPhoneNums(), dto.getMsgMark(),dto.getParams().toArray(new String[0]));
        return ResultVO.success();
    }


}

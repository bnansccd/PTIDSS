package com.troy.system.controller.web;


import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.system.domain.DTO.MessageDTO;
import com.troy.system.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "短信接口")
@RequestMapping(value = UrlConstants.WEB_RESTFUL)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("获取消息模板")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"message/messageTemplate")
    public ResultVO messageTemplate() {
        return ResultVO.success(messageService.findTemplate());
    }

    @ApiOperation("下达指令")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"message/order")
    public ResultVO order(@RequestBody MessageDTO messageDTO) {
        return ResultVO.success(messageService.sendMessage(messageDTO));
    }


}

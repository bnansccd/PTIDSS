package com.troy.sync.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.sync.domain.DTO.SyncTargetDTO;
import com.troy.sync.service.TargetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenxl
 * @description
 * @date 2024-09-06 10:44
 */
@Controller
@Api(tags = "同步按钮")
@RestController
@RequestMapping(value = UrlConstants.WEB_RESTFUL+"synchronize/")
public class TargetController {


    @Autowired
    private TargetService targetService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sync")
    public ResultVO sync(@RequestBody @Validated SyncTargetDTO dto){
        targetService.syncTarget(dto);
        return ResultVO.success();
    }

}

package com.troy.sync.controller.web;

import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.oauth2.annotation.OauthApi;
import com.troy.sync.domain.DTO.SyncScriptDTO;
import com.troy.sync.service.SyncService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-08-19 14:18
 */
@Controller
@Api(tags = "外部调用同步数据")
@RestController
@RequestMapping(value = UrlConstants.THIRD_RESTFUL+"external/")
public class SyncRpcController {

    @Autowired
    private SyncService syncService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sync")
    @OauthApi
    public ResultVO<List<Row>> sync(@RequestBody SyncScriptDTO dto){
        return ResultVO.success(syncService.getSyncScript(dto));
    }

}

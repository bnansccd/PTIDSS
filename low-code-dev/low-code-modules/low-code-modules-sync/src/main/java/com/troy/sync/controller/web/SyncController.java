package com.troy.sync.controller.web;


import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.sync.domain.DTO.SyncDTO;
import com.troy.sync.domain.DTO.SyncScriptDTO;
import com.troy.sync.service.SyncService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(tags = "数据同步")
@RestController
@RequestMapping(value = UrlConstants.WEB_RESTFUL+UrlConstants.RESTFUL_VERSION_V1)
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("sync")
    public ResultVO sync(@RequestBody SyncDTO dto){
        syncService.sync(dto);
        return ResultVO.success();
    }

    @PostMapping("syncScript")
    public ResultVO sync(@RequestBody SyncScriptDTO dto){
        syncService.syncScript(dto);
        return ResultVO.success();
    }


    @PostMapping("syncRpc")
    public ResultVO syncRpc(@RequestBody SyncDTO dto){
        syncService.syncRpc(dto);
        return ResultVO.success();
    }

    @PostMapping("syncScriptRpc")
    public ResultVO syncScriptRpc(@RequestBody SyncScriptDTO dto){
        syncService.syncScriptRpc(dto);
        return ResultVO.success();
    }
}

package com.troy.sync.controller.rpc;


import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.sync.api.domain.DTO.SearchDTO;
import com.troy.sync.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping(value = UrlConstants.RPC_RESTFUL+UrlConstants.RESTFUL_VERSION_V1)
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("getSync")
    public ResultVO<List<Row>> sync(@RequestParam("tableName") String tableName, @RequestBody SearchDTO condition) {
        return ResultVO.success(syncService.sync(tableName, condition));
    }


    @PostMapping("getSyncIncrease")
    public ResultVO<List<Row>> getSyncIncrease(@RequestParam("tableName") String tableName, @RequestBody SearchDTO condition) {
        return ResultVO.success(syncService.getSyncIncrease(tableName, condition));
    }


    @PostMapping("getSyncByScript")
    public ResultVO<List<Row>> getSyncByScript(@RequestBody String script) {
        return ResultVO.success(syncService.findByScript(script));
    }

}

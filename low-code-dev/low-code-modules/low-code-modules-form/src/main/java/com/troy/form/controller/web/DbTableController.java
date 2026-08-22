package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.DbTableService;
import com.troy.form.domain.DTO.DbTableSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class DbTableController {

    @Autowired
    private DbTableService dbTableService;

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"table")
    public ResultVO list(@Validated DbTableSearchDTO dto) {
        return ResultVO.success(dbTableService.getList(dto));
    }

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"table/{dataBaseId}")
    public ResultVO addTable(@PathVariable Long dataBaseId, @RequestParam String tableName) {
        dbTableService.addTable(dataBaseId, tableName);
        return ResultVO.success();
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"table/sql/{tableId}")
    public ResultVO sql(@PathVariable Long tableId) {
        return ResultVO.success(dbTableService.getSQl(tableId));
    }

}

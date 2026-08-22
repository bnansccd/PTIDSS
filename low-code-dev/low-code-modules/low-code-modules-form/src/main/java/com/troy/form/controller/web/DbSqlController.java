package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.DbSqlService;
import com.troy.form.domain.DTO.DbSqlDTO;
import com.troy.form.domain.DTO.DbSqlSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class DbSqlController {

    @Autowired
    private DbSqlService dbSqlService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sql")
    public ResultVO add(@RequestBody DbSqlDTO dto) {
        dbSqlService.addSql(dto);
        return ResultVO.success();
    }

    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sql/{id}")
    public ResultVO update(@PathVariable Long id, @RequestBody DbSqlDTO dto) {
        dbSqlService.updateSql(id, dto);
        return ResultVO.success();
    }


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sql")
    public ResultVO page(DbSqlSearchDTO dto) {
        return ResultVO.success(dbSqlService.getPage(dto));
    }


}

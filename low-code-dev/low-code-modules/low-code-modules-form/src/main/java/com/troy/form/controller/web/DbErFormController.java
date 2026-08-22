package com.troy.form.controller.web;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.DbErFormService;
import com.troy.form.domain.DTO.DbErFormDTO;
import com.troy.form.domain.DTO.DbErFormSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-09 09:45:50
 */
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class DbErFormController {

    @Autowired
    private DbErFormService dbErFormService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"form")
    public ResultVO save(@Validated @RequestBody DbErFormDTO dbErFormDTO) {
        dbErFormService.addForm(dbErFormDTO);
        return ResultVO.success();
    }

    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"form/{id}")
    public ResultVO update(@PathVariable Long id, @Validated @RequestBody DbErFormDTO dbErFormDTO) {
        dbErFormService.updateForm(id, dbErFormDTO);
        return ResultVO.success();
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"form")
    public ResultVO page(DbErFormSearchDTO dbErFormDTO) {
        return ResultVO.success(dbErFormService.findPage(dbErFormDTO));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"form/checkIn/{id}")
    public ResultVO checkIn(@PathVariable Long id) {
        dbErFormService.lockForm(id, Constants.FALSE);
        return ResultVO.success();
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"form/checkOut/{id}")
    public ResultVO checkOut(@PathVariable Long id) {
        dbErFormService.lockForm(id, Constants.TRUE);
        return ResultVO.success();
    }

//    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"form/test")
//    public ResultVO test() {
//
//    }
}

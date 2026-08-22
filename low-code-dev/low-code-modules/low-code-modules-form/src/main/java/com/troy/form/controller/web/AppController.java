package com.troy.form.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.form.service.AppService;
import com.troy.form.domain.DTO.AppDTO;
import com.troy.form.domain.DTO.AppSearchDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Api(tags = "app管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
public class AppController {

    @Autowired
    private AppService appService;

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"app")
    public ResultVO save(@RequestBody AppDTO appDTO) {
        appService.saveAppEntity(appDTO);
        return ResultVO.success();
    }


    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"app/{ids}")
    public ResultVO remove(@PathVariable List<Long> ids) {
        appService.deleteAppEntity(ids);
        return ResultVO.success();
    }


    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"app/{id}")
    public ResultVO update(@PathVariable Long id, @RequestBody AppDTO appDTO) {
        appService.updateAppEntity(id, appDTO);
        return ResultVO.success();
    }


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"app")
    public ResultVO list(AppSearchDTO appSearchDTO) {
        return ResultVO.success(appService.findAllApps(appSearchDTO));
    }


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"app/{id}")
    public ResultVO getInfo(@PathVariable Serializable id) {
        return ResultVO.success();
    }


}

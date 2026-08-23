package com.troy.system.controller.web;


import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import com.troy.system.api.domain.VO.SysAppVO;
import com.troy.system.domain.DTO.SysAppDTO;
import com.troy.system.domain.DTO.SysAppQueryDTO;
import com.troy.system.service.SysAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 官网资讯表 前端控制器
 * </p>
 *
 * @author chenxl
 * @since 2023-03-13
 */
@Api(tags = "应用管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysAppController {

    @Autowired
    private SysAppService sysAppService;

    @ApiOperation(value = "通过id查询应用")
    @ApiImplicitParam(value = "id", name = "应用Id", required = true, paramType = "path")
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/{id}")
    public ResultVO getApp(@PathVariable Long id) {
        return ResultVO.success(sysAppService.getSysApp(id));
    }

    @ApiOperation(value = "查询应用")
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp")
    public ResultVO getAppPage(@Validated SysAppQueryDTO dto) {
        return ResultVO.success(sysAppService.getSysAppPage(dto));
    }

    @ApiOperation(value = "更新应用")
    @PutMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/{id}")
    public ResultVO updateApp(@PathVariable("id") Long id, @RequestBody @Validated SysAppDTO dto) {
        return sysAppService.updateSysApp(id, dto);
    }

    @ApiOperation(value = "删除应用")
    @DeleteMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/{ids}")
    public ResultVO deleteApp(@PathVariable("ids") List<Long> ids) {
        return sysAppService.deleteSysApp(ids);
    }

    @ApiOperation(value = "新增应用")
    @PostMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp")
    public ResultVO addApp(@RequestBody @Validated SysAppDTO dto) {
        return sysAppService.addSysApp(dto);
    }

    @ApiOperation(value = "应用状态变更")
    @PatchMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/status/{ids}")
    public ResultVO updateApp(
            @PathVariable("ids") List<Long> ids,
            @RequestParam("status") @NotBlank(message = "请选择启停状态") @ValidDict(parentType = DictTypeEnums.STATUS_TYPE,message = "请选择正确的启停状态")  String status
    ) {
        return ResultVO.success(sysAppService.updateStatus(ids, status));
    }

    @ApiOperation(value = "重置应用密钥")
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/reset/{id}")
    public ResultVO<String> updateApp(@PathVariable("id") Long id) {
        return ResultVO.success(sysAppService.reset(id));
    }

    @ApiOperation(value = "查看应用密钥信息")
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/getKey/{id}")
    public ResultVO<HashMap<String, String>> getKey(@PathVariable("id") Long id) {
        return ResultVO.success(sysAppService.getKey(id));
    }

    @ApiOperation(value = "得到当前登录人所拥有的应用")
    @GetMapping(value = UrlConstants.RESTFUL_VERSION_V1 + "sysApp/current")
    public ResultVO<List<SysAppVO>> currentApp() {
        return ResultVO.success(this.sysAppService.currentApp());
    }



}


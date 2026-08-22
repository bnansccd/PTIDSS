package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.api.domain.VO.SysTenantVO;
import com.troy.system.domain.DTO.*;
import com.troy.system.domain.VO.TenantMenuVO;
import com.troy.system.service.SysTenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/22 10:10:21
 * @Description: SysTenantController
 * @Version: 1.0.0
 */
@Api(tags = "租户操作类")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysTenantController {

    @Autowired
    private SysTenantService sysTenantService;

    @ApiOperation(value = "分页查询租户")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant")
    public ResultVO<PageVO<SysTenantVO>> listPage(@Validated SysTenantSearchDTO dto) {
        return ResultVO.success(this.sysTenantService.listPage(dto));
    }

    @Log(title = "添加租户并初始化帐号", businessType = BusinessType.INSERT)
    @ApiOperation(value = "添加租户并初始化帐号")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant")
    public ResultVO insert(@RequestBody @Validated SysTenantInsertDTO dto) {
        return this.sysTenantService.insert(dto);
    }

    @ApiOperation(value = "通过主键查询租户基础信息")
    @ApiImplicitParam(value = "主键",name = "id",required = true,paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/{id}")
    public ResultVO<SysTenantVO> findById(@PathVariable Long id) {
        return ResultVO.success(this.sysTenantService.findById(id));
    }

    @Log(title = "编辑租户", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "编辑租户")
    @ApiImplicitParam(value = "主键",name = "id",required = true,paramType = "path")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/{id}")
    public ResultVO edit(@PathVariable Long id,@Validated @RequestBody SysTenantDTO dto) {
        return this.sysTenantService.edit(id, dto);
    }

    @Log(title = "修改租户启停状态", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改租户启停状态")
    @ApiImplicitParam(value = "主键",name = "id",required = true,paramType = "path")
    @PatchMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/{id}")
    public ResultVO editStatus(@PathVariable Long id) {
        return this.sysTenantService.editStatus(id);
    }

    @Log(title = "删除租户", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除租户")
    @ApiImplicitParam(value = "多个主键",name = "ids",required = true,paramType = "path")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/ids/{ids}")
    public ResultVO deleteByIdIn(@PathVariable List<Long> ids) {
        return this.sysTenantService.deleteByIdIn(ids);
    }


    @ApiOperation(value = "绑定租户APP")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/bindApp")
    public ResultVO bindApp(@RequestBody @Validated TenantAppDTO tenantAppDTO) {
        this.sysTenantService.bindTenant(tenantAppDTO);
        return ResultVO.success();
    }

    @ApiOperation(value = "更新租户APP绑定")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/bindApp/{id}")
    public ResultVO bindApp(@PathVariable Long id, @RequestBody @Validated TenantAppDTO tenantAppDTO) {
        this.sysTenantService.bindTenant(id, tenantAppDTO);
        return ResultVO.success();
    }



    @ApiOperation(value = "解绑租户APP")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/unbindApp")
    public ResultVO unbindApp(@RequestBody @Validated TenantAppDTO tenantAppDTO) {
        this.sysTenantService.deleteTenant(tenantAppDTO);
        return ResultVO.success();
    }

    @ApiOperation(value = "获取用户应用")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/getApp")
    public ResultVO getApp(@Validated TenantAppSearchDTO dto) {
        return ResultVO.success(sysTenantService.findTenantAppPage(dto));
    }

    @ApiOperation(value = "获取当前用户应用")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/getCurrentApp")
    public ResultVO getCurrentApp(@Validated TenantAppSearchDTO dto) {
        return ResultVO.success(sysTenantService.findCurrentTenantAppPage(dto));
    }

    @ApiOperation(value = "获取租户-app-菜单")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/getAppMenu")
    public ResultVO<List<TenantMenuVO>> getAppMenu(TenantAppDTO dto) {
        return ResultVO.success(sysTenantService.getTenantMenu(dto));
    }

    @ApiOperation(value = "绑定租户-APP-菜单")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/bindMenu/{tenantId}")
    public ResultVO bindMenu(@PathVariable Long tenantId, @RequestBody List<TenantMenuDTO> list) {
        this.sysTenantService.updateTenantMenu(tenantId, list);
        return ResultVO.success();
    }


    @ApiOperation(value = "获取当前用户菜单")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysTenant/getCurrentAppMenu")
    public ResultVO<List<SysMenuVO>> getCurrentAppMenu(TenantAppDTO dto) {
        return ResultVO.success(sysTenantService.getCurrentAppMenu(dto));
    }



}

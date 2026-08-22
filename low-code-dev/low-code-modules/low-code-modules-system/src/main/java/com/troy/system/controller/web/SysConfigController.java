package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysConfigVO;
import com.troy.system.domain.DTO.SysConfigDTO;
import com.troy.system.domain.DTO.SysConfigQueryDTO;
import com.troy.system.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 参数配置表 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */

@Api(tags = "参数配置表")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @ApiOperation(value = "参数配置分页列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig")
    public ResultVO<PageVO<SysConfigVO>> getSysConfigPage(SysConfigQueryDTO dto){
        return ResultVO.success(this.sysConfigService.getSysConfigPage(dto));
    }

    @ApiOperation(value = "新增参数配置")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig")
    @Log(title = "新增参数配置", businessType = BusinessType.INSERT)
    public ResultVO insertSysConfig(@Validated @RequestBody SysConfigDTO dto){
        return this.sysConfigService.insertSysConfig(dto);
    }

    @ApiOperation(value = "查看参数配置详情")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig/{id}")
    public ResultVO<SysConfigVO> getSysConfigById(@PathVariable Long id){
        return ResultVO.success(this.sysConfigService.getSysConfigById(id));
    }

    @ApiOperation(value = "编辑参数配置")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig/{id}")
    @Log(title = "编辑参数配置", businessType = BusinessType.UPDATE)
    public ResultVO editSysConfig(@PathVariable Long id,@Validated @RequestBody SysConfigDTO dto){
        return sysConfigService.editSysConfig(id,dto);
    }

    @ApiOperation(value = "删除参数配置")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig/{ids}")
    @Log(title = "删除参数配置", businessType = BusinessType.DELETE)
    @ApiImplicitParam(value = "ids",name = "ids",required = true,paramType = "path")
    public ResultVO deleteSysConfigById(@PathVariable @NotEmpty(message = "请选择要删除的参数配置！") @Size(max = 1000,message = "一次最多删除100条数据！") List<Long> ids){
        return this.sysConfigService.deleteSysConfigById(ids);
    }

    @ApiOperation(value = "通过配置key查询配置（不传参数查所有）")
    @ApiImplicitParam(value = "配置key",name = "configKeys",paramType = "query")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysConfig/configKeys")
    public ResultVO<List<SysConfigVO>> getSysConfigByConfigKeyIn(@RequestParam(required=false) List<String> configKeys){
        return ResultVO.success(this.sysConfigService.findBySysConfigByConfigKeyIn(configKeys));
    }
}

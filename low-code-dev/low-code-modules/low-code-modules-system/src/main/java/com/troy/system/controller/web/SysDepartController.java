package com.troy.system.controller.web;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.domain.DTO.SysDepartDTO;
import com.troy.system.domain.DTO.SysDepartQueryDTO;
import com.troy.system.domain.DTO.SysDepartSearchDTO;
import com.troy.system.service.SysDepartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysDepartController {

    @Autowired
    private SysDepartService sysDepartService;

    @ApiModelProperty(value = "部门列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart")
    public ResultVO<List<SysDepartVO>> getSysDepartList(SysDepartQueryDTO dto){
        return ResultVO.success(this.sysDepartService.getSysDepartList(dto));
    }

    @ApiModelProperty(value = "部门分页")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepartPage")
    public ResultVO<PageVO<SysDepartVO>> sysDepartPage(SysDepartSearchDTO dto){
        return ResultVO.success(this.sysDepartService.findPage(dto));
    }

    @ApiModelProperty(value = "部门树形列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/tree")
    public ResultVO<List<SysDepartVO>> getSysDepartTree(@RequestParam(required = false) String sfqy){
        return ResultVO.success(this.sysDepartService.getSysDepartTree(sfqy));
    }

    @ApiModelProperty(value = "新增部门")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart")
    @Log(title = "新增部门", businessType = BusinessType.INSERT)
    public ResultVO insertSysDepart(@Validated @RequestBody SysDepartDTO dto){
        return this.sysDepartService.insertSysDepart(dto);
    }

    @ApiOperation(value = "查询部门最大排序号")
    @ApiImplicitParam(value = "parentId",name = "parentId", paramType = "query")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/sort")
    public ResultVO sort(Long parentId){
        return ResultVO.success(this.sysDepartService.getCurrentSort(parentId));
    }


    @ApiModelProperty(value = "查看部门基础信息")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/{id}")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    public ResultVO<SysDepartVO> findById(@PathVariable Long id){
        return ResultVO.success(this.sysDepartService.findById(id));
    }

    @ApiModelProperty(value = "编辑部门")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/{id}")
    @ApiImplicitParam(value = "id",name = "id",required = true, paramType = "path")
    @Log(title = "编辑部门", businessType = BusinessType.UPDATE)
    public ResultVO updateSysDepartById(@PathVariable Long id,@Validated @RequestBody SysDepartDTO dto){
        return this.sysDepartService.updateSysDepartById(id,dto);
    }

    @ApiModelProperty(value = "批量删除部门")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/{ids}")
    @Log(title = "批量删除部门", businessType = BusinessType.DELETE)
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    public ResultVO deleteSysDepartById(@PathVariable @NotEmpty(message = "请选择要删除的部门") @Size(max = 1000,message = "一次最多删除1000个部门") List<Long> ids){
        return this.sysDepartService.deleteSysDepartById(ids);
    }

    @ApiModelProperty(value = "设置启用")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/enable/{ids}")
    @ApiImplicitParam(value = "id",name = "id",required = true, paramType = "path")
    @Log(title = "编辑部门启用状态", businessType = BusinessType.UPDATE)
    public ResultVO enable(@PathVariable List<Long> ids){
        sysDepartService.updateEnable(ids, Constants.ZERO_STR);
        return ResultVO.success();
    }

    @ApiModelProperty(value = "设置停用")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/disable/{ids}")
    @ApiImplicitParam(value = "id",name = "id",required = true, paramType = "path")
    @Log(title = "编辑部门启用状态", businessType = BusinessType.UPDATE)
    public ResultVO disable(@PathVariable List<Long> ids){
        sysDepartService.updateEnable(ids, Constants.ONE_STR);
        return ResultVO.success();
    }


}

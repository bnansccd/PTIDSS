package com.troy.system.controller.web;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysPostVO;
import com.troy.system.domain.DTO.SysPostDTO;
import com.troy.system.domain.DTO.SysPostQueryDTO;
import com.troy.system.service.SysPostService;
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
 * 岗位管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "岗位管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysPostController {

    @Autowired
    private SysPostService sysPostService;

    @ApiOperation(value = "岗位列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost")
    public ResultVO<PageVO<SysPostVO>> getSysPostList(SysPostQueryDTO dto){
        return ResultVO.success(this.sysPostService.getSysPostList(dto));
    }

    @ApiOperation(value = "新增岗位")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost")
    @Log(title = "新增岗位", businessType = BusinessType.INSERT)
    public ResultVO insertSysPost(@Validated @RequestBody SysPostDTO dto){
        return this.sysPostService.insertSysPost(dto);
    }

    @ApiOperation(value = "获取岗位最大排序")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/sort")
    public ResultVO sort(){
        return ResultVO.success(sysPostService.getCurrentSort());
    }

    @ApiOperation(value = "查看岗位详情")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/{id}")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    public ResultVO<SysPostVO> getSysPostById(@PathVariable Long id){
        return ResultVO.success(this.sysPostService.getSysPostById(id));
    }

    @ApiOperation(value = "编辑岗位")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/{id}")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    @Log(title = "编辑岗位", businessType = BusinessType.UPDATE)
    public ResultVO updateSysPostById(@PathVariable Long id,@Validated @RequestBody SysPostDTO  dto){
        return this.sysPostService.updateSysPostById(id,dto);
    }

    @ApiOperation(value = "批量删除岗位")
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/{ids}")
    @ApiImplicitParam(value = "ids",name = "ids",required = true,paramType = "path")
    @Log(title = "批量删除岗位", businessType = BusinessType.DELETE)
    public ResultVO deleteSysPostById(@PathVariable @NotEmpty(message = "请选择要删除的岗位") @Size(max = 1000,message = "一次最多删除100个岗位") List<Long> ids){
        return this.sysPostService.deleteSysPostById(ids);
    }


    @ApiModelProperty(value = "设置启用")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/enable/{ids}")
    @ApiImplicitParam(value = "id",name = "id",required = true, paramType = "path")
    @Log(title = "编辑岗位启用状态", businessType = BusinessType.UPDATE)
    public ResultVO enable(@PathVariable List<Long> ids){
        sysPostService.updateEnable(ids, Constants.ZERO_STR);
        return ResultVO.success();
    }

    @ApiModelProperty(value = "设置停用")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysPost/disable/{ids}")
    @ApiImplicitParam(value = "id",name = "id",required = true, paramType = "path")
    @Log(title = "编辑岗位停用状态", businessType = BusinessType.UPDATE)
    public ResultVO disable(@PathVariable List<Long> ids){
        sysPostService.updateEnable(ids, Constants.ONE_STR);
        return ResultVO.success();
    }

}

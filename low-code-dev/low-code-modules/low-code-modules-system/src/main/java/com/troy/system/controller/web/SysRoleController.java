package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysRoleVO;
import com.troy.system.domain.DTO.SysRoleDTO;
import com.troy.system.domain.DTO.SysRoleDataRangeDTO;
import com.troy.system.domain.DTO.SysRoleQueryDTO;
import com.troy.system.service.SysRoleMenuService;
import com.troy.system.service.SysRoleService;
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
 * 角色管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @ApiOperation(value = "角色列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole")
    public ResultVO<PageVO<SysRoleVO>> getSysRoleList(SysRoleQueryDTO dto){
        return ResultVO.success(this.sysRoleService.getSysRoleList(dto));
    }

    @ApiOperation(value = "新增角色")
    @Log(title = "新增角色", businessType = BusinessType.INSERT)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole")
    public ResultVO insertSysRole(@Validated @RequestBody SysRoleDTO dto){
        return this.sysRoleService.insertSysRole(dto);
    }

    @ApiOperation(value = "获取触角排序最大排序号")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole/sort")
    public ResultVO sort(){
        return ResultVO.success(sysRoleService.getCurrentSort());
    }


    @ApiOperation(value = "查看角色详情")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole/{id}")
    public ResultVO<SysRoleVO> getSysRoleById(@PathVariable Long id){
        return ResultVO.success(this.sysRoleService.getSysRoleById(id));
    }

    @ApiOperation(value = "编辑角色")
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    @Log(title = "编辑角色", businessType = BusinessType.UPDATE)
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole/{id}")
    public  ResultVO updateSysRoleById(@PathVariable Long id,@Validated @RequestBody SysRoleDTO dto){
        return this.sysRoleService.updateSysRoleById(id,dto);
    }

    @ApiOperation(value = "批量删除角色")
    @ApiImplicitParam(value = "ids",name = "ids",required = true,paramType = "path")
    @Log(title = "批量删除角色", businessType = BusinessType.DELETE)
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole/batch/{ids}")
    public ResultVO deleteSysRoleById(@PathVariable @NotEmpty(message = "请选择要删除的角色！") @Size(max = 1000,message = "一次最多删除1000条角色数据！")List<Long> ids){
        return this.sysRoleService.deleteSysRoleById(ids);
    }

    @ApiOperation(value = "配置数据权限")
    @Log(title = "配置数据权限", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(value = "id",name = "id",required = true,paramType = "path")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysRole/dataRange/{id}")
    public ResultVO updateSysRoleDataRange(@PathVariable Long id, @Validated @RequestBody SysRoleDataRangeDTO dto){
        return this.sysRoleService.updateSysRoleDataRange(id,dto);
    }

}

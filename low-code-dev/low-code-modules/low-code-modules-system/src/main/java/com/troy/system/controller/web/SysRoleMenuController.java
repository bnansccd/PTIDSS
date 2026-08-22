package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.domain.DTO.TenantMenuDTO;
import com.troy.system.service.SysRoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/12 14:14:52
 * @Description: SysRoleMenuController
 * @Version: 1.0.0
 */
@Api(tags = "角色与菜单的关系")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @ApiOperation(value = "通过角色id查询角色与菜单的关系")
    @ApiImplicitParam(value = "角色id", name = "roleId", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysRoleMenu/roleId/{roleId}")
    public ResultVO SysRoleMenuByRoleId(@PathVariable Long roleId) {
        return ResultVO.success(this.sysRoleMenuService.SysRoleMenuByRoleId(roleId));
    }

    @ApiOperation(value = "配置角色菜单权限")
    @ApiImplicitParam(value = "roleId", name = "roleId", required = true, paramType = "path")
    @Log(title = "配置角色菜单权限", businessType = BusinessType.UPDATE)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysRoleMenu/roleId/{roleId}")
    public ResultVO insertRoleMenu(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        return this.sysRoleMenuService.insertRoleMenu(roleId, menuIds);
    }

    @ApiOperation(value = "配置角色菜单权限-new")
    @Log(title = "配置角色菜单权限", businessType = BusinessType.UPDATE)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysRoleMenu/roleId/app/{roleId}")
    public ResultVO insertAppRoleMenu(@PathVariable Long roleId, @RequestBody List<TenantMenuDTO> list) {
        sysRoleMenuService.insertAppMenu(roleId, list);
        return ResultVO.success();
    }

    @ApiOperation(value = "通过角色id查询角色与菜单的关系-new")
    @ApiImplicitParam(value = "角色id", name = "roleId", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysRoleMenu/roleId/app/{roleId}")
    public ResultVO sysRoleMenuByRoleId(@PathVariable Long roleId) {
        return ResultVO.success(this.sysRoleMenuService.appMenuByRoleId(roleId));
    }

}

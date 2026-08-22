package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.api.domain.VO.SysMenuVO;
import com.troy.system.domain.DTO.SysMenuDTO;
import com.troy.system.domain.DTO.SysMenuQueryDTO;
import com.troy.system.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation(value = "菜单列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu")
    public ResultVO<List<SysMenuVO>> getSysMenuList(SysMenuQueryDTO dto) {
        return ResultVO.success(this.sysMenuService.getSysMenuList(dto));
    }

    @ApiOperation(value = "菜单树形列表")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/tree")
    public ResultVO<List<SysMenuVO>> getSysMenuTree() {
        return ResultVO.success(this.sysMenuService.getNewSysMenuTree());
    }

    @ApiOperation(value = "菜单树形列表-new")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/tree/{appId}")
    public ResultVO<List<SysMenuVO>> getSysMenuTree(@PathVariable Long appId) {
        return ResultVO.success(this.sysMenuService.getSysMenuTree(appId));
    }

    @ApiOperation(value = "通过appId查询可选菜单")
    @ApiImplicitParam(value = "应用id", name = "appId", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/optional/appId/{appId}")
    public ResultVO<List<SysMenuVO>> findByAppIdOptional(@PathVariable("appId") Long appId) {
        return ResultVO.success(this.sysMenuService.findByAppIdOptional(appId));
    }

    @ApiOperation(value = "通过appId查询已经配置的菜单")
    @ApiImplicitParam(value = "应用id", name = "appId", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/appId/{appId}")
    public ResultVO<List<SysMenuVO>> findByAppId(@PathVariable("appId") Long appId) {
        return ResultVO.success(this.sysMenuService.findByAppId(appId));
    }


    @ApiOperation(value = "新增菜单")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu")
    @Log(title = "新增菜单", businessType = BusinessType.INSERT)
    public ResultVO insertSysMenu(@Validated @RequestBody SysMenuDTO dto) {
        return this.sysMenuService.insertSysMenu(dto);
    }

    @ApiOperation(value = "查询菜单最大排序号")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/sort")
    @ApiImplicitParam(value = "parentId", name = "parentId", paramType = "path")
    public ResultVO sort(Long parentId) {
        return ResultVO.success(this.sysMenuService.getCurrentSort(parentId));
    }


    @ApiOperation(value = "查看菜单详情")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/{id}")
    public ResultVO<SysMenuVO> getSysMenuById(@PathVariable Long id) {
        return ResultVO.success(this.sysMenuService.getSysMenuById(id));
    }

    @ApiOperation(value = "编辑菜单")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @Log(title = "编辑菜单", businessType = BusinessType.UPDATE)
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/{id}")
    public ResultVO updateSysMenuById(@PathVariable Long id, @Validated @RequestBody SysMenuDTO dto) {
        return this.sysMenuService.updateSysMenuById(id, dto);
    }

    @ApiOperation(value = "删除菜单")
    @ApiImplicitParam(value = "ids", name = "ids", required = true, paramType = "path")
    @Log(title = "删除菜单", businessType = BusinessType.DELETE)
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/{ids}")
    public ResultVO deleteById(@PathVariable @NotEmpty(message = "请选择要删除的菜单！") @Size(max = 1000, message = "一次最多删除1000条菜单数据！") List<Long> ids) {
        return this.sysMenuService.deleteById(ids);
    }

    @ApiOperation(value = "单个启用停用")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @Log(title = "单个启用停用", businessType = BusinessType.UPDATE)
    @PatchMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/{id}")
    public ResultVO updateSysMenuStatus(@PathVariable Long id) {
        return this.sysMenuService.updateSysMenuStatus(id);
    }

    @ApiOperation(value = "菜单展示隐藏")
    @ApiImplicitParam(value = "id", name = "id", required = true, paramType = "path")
    @Log(title = "菜单展示隐藏", businessType = BusinessType.UPDATE)
    @PatchMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/isShow/{id}")
    public ResultVO updateSysMenuIsShow(@PathVariable Long id) {
        return this.sysMenuService.updateSysMenuIsShow(id);
    }


    @ApiOperation(value = "绑定APP")
    @Log(title = "绑定APP", businessType = BusinessType.GRANT)
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysMenu/bindApp/{appId}")
    public ResultVO bindApp(@PathVariable Long appId, @RequestBody(required = false) Set<Long> ids) {
        return sysMenuService.bindApp(appId,ids);
    }

    @ApiOperation(value = "获取租户所拥有的权限")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysMenu/tenantId/{tenantId}")
    public ResultVO<List<SysMenuVO>> findByTenantId(@PathVariable Long tenantId){
        return ResultVO.success(this.sysMenuService.findByTenantId(tenantId));
    }

    @Log(title = "绑定租户所拥有的权限", businessType = BusinessType.GRANT)
    @ApiOperation(value = "绑定租户所拥有的权限")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "tenantId",value = "租户Id",required = true,paramType = "path"),
                    @ApiImplicitParam(name = "权限编码",value = "menuCodes",required = true,paramType = "body")
            }
    )
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1+"sysMenu/tenantId/{tenantId}")
    public ResultVO bindTenant(@PathVariable Long tenantId, @RequestBody(required = false) Set<String> menuCodes) {
        return sysMenuService.bindTenant(tenantId,menuCodes);
    }

}

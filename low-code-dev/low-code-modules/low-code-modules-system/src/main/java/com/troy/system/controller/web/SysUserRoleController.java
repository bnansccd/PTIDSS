package com.troy.system.controller.web;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.system.service.SysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname: SysUserRoleController
 * @Description:
 * @Date 2022/9/20
 * @Author: yzy
 * @Version
 **/
@Api(tags = {"用户角色"})
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysUserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @ApiOperation(value = "配置用户角色")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysUserRole/{userId}")
    @ApiImplicitParam(value = "userId",name = "userId",required = true,paramType = "path")
    @Log(title = "配置用户角色", businessType = BusinessType.INSERT)
    public ResultVO insertSysUserRole(@PathVariable Long userId, @RequestBody List<Long> roleIds){
        return this.sysUserRoleService.insertUserRoleByUserId(userId,roleIds);
    }
}

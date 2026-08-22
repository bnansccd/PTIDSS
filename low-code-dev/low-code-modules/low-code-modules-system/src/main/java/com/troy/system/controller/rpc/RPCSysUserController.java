package com.troy.system.controller.rpc;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.DTO.RegisterDTO;
import com.troy.system.api.domain.VO.AuditVO;
import com.troy.system.api.domain.VO.SysUserDetailsVO;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "RPC-用户管理")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCSysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "通过用户名查询用户")
    @InnerAuth
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "username", value = "账号", required = true, paramType = "path"),
                    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "path"),
            }
    )
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/username/{username}/tenantId/{tenantId}")
    public ResultVO<SysUserDetailsVO> sysUserByUsernameAndTenantId(@PathVariable("username") String username, @PathVariable("tenantId") Long tenantId) {
        return ResultVO.success(this.sysUserService.sysUserByUsernameAndTenantId(username, tenantId));
    }

    @ApiOperation(value = "通过手机号查询用户")
    @InnerAuth
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "phone", value = "手机号", required = true, paramType = "path"),
                    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "path"),
            }
    )
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/phone/{phone}/tenantId/{tenantId}")
    public ResultVO<SysUserDetailsVO> sysUserByPhoneAndTenantId(@PathVariable("phone") String phone, @PathVariable("tenantId") Long tenantId) {
        return ResultVO.success(this.sysUserService.sysUserByPhoneAndTenantId(phone, tenantId));
    }

    @ApiOperation(value = "用户注册")
    @InnerAuth
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/register")
    public ResultVO sysUserRegister(@RequestBody @Validated RegisterDTO dto) {
        return this.sysUserService.sysUserRegister(dto);
    }

    @ApiOperation(value = "查询审计信息")
    @InnerAuth
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "audit/info")
    public ResultVO<AuditVO> findAuditInfo(@RequestBody AuditDTO dto) {
        return ResultVO.success(this.sysUserService.findAuditInfo(dto));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/realName/{realName}/tenantId/{tenantId}")
    @InnerAuth
    ResultVO<List<SysUserVO>> sysUserByRealNameAndTenantId(@PathVariable("realName") String realName, @PathVariable("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        return ResultVO.success(this.sysUserService.getListByConditionAndTenantId(realName, tenantId));
    }


    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byIds")
    @InnerAuth
    ResultVO<List<SysUserVO>> sysUserByIds(@RequestParam("ids") List<Long> ids, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        return ResultVO.success(this.sysUserService.getByIds(ids, tenantId));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/{id}")
    ResultVO<SysUserVO> findById(@PathVariable("id") Long id) {
        return ResultVO.success(this.sysUserService.findById(id));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byDepartIdsAndUsername")
    @InnerAuth
    ResultVO<List<SysUserVO>> byDepartIdsAndUsername(@RequestParam(value = "ids", required = false) List<Long> ids, @RequestParam(value = "username", required = false) String name, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        SecurityContextHolder.setTenantId(tenantId);
        return ResultVO.success(this.sysUserService.byDepartIdsAndUsername(ids, name));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/byDepartIdsAndRealName")
    @InnerAuth
    ResultVO<List<SysUserVO>> byDepartIdsAndRealName(@RequestParam(value = "ids", required = false) List<Long> ids, @RequestParam(value = "username", required = false) String name, @RequestParam("tenantId") Long tenantId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        SecurityContextHolder.setTenantId(tenantId);
        return ResultVO.success(this.sysUserService.byDepartIdsAndRealName(ids, name));
    }

    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysUser/getAll")
    @InnerAuth
    ResultVO<List<SysUserVO>> getAll(@RequestHeader(SecurityConstants.FROM_SOURCE) String source){
        return ResultVO.success(sysUserService.getAll());
    }

    @PostMapping(UrlConstants.RESTFUL_VERSION_V1+"sysUser/getByRealNameIn")
    @InnerAuth
    ResultVO<List<SysUserVO>> getByRealNameIn(@RequestBody List<String> names,@RequestHeader(SecurityConstants.FROM_SOURCE) String source){
        return ResultVO.success(sysUserService.getByRealNameIn(names));
    }
}

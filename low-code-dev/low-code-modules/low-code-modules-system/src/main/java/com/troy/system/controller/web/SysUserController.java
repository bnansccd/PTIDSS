package com.troy.system.controller.web;

import com.troy.common.core.anotation.Sensitive;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.log.annotation.Log;
import com.troy.common.log.enums.BusinessType;
import com.troy.common.redis.service.RedisService;
import com.troy.common.security.annotation.ValidDict;
import com.troy.system.api.domain.VO.SysUserDetailsVO;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.domain.DTO.SysUserDTO;
import com.troy.system.domain.DTO.SysUserPageQueryDTO;
import com.troy.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.troy.common.core.constant.CacheConstants.LOGIN_LOCK_KEY;

/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping(UrlConstants.WEB_RESTFUL)
@Validated
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "得到当前登录用户信息")
    @ApiImplicitParam(value = "应用Id", name = "appId", paramType = "query")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "current")
    public ResultVO<SysUserDetailsVO> current(Long appId) {
        return ResultVO.success(this.sysUserService.current(appId));
    }

    @ApiOperation(value = "根据编码得到当前登录用户信息")
    @ApiImplicitParam(value = "应用Id", name = "appId", paramType = "query")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "currentByCode")
    public ResultVO<SysUserDetailsVO> current(String appCode) {
        return ResultVO.success(this.sysUserService.current(appCode));
    }

    @ApiOperation(value = "用户列表(分页)")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser")
    @Sensitive
    public ResultVO<PageVO<SysUserDetailsVO>> getSysUserList(SysUserPageQueryDTO dto) {
        return ResultVO.success(this.sysUserService.getSysUserList(dto));
    }

    @ApiOperation(value = "用户列表（用于前端下拉选择）")
    @ApiImplicitParam(value = "查询参数，包含（用户名、真空姓名、手机号）", name = "queryParams", paramType = "query")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/list")
    public ResultVO<List<SysUserVO>> getSysUserList(String queryParams) {
        return ResultVO.success(this.sysUserService.getListByCondition(queryParams));
    }

    @ApiOperation(value = "新增用户")
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser")
    @Log(title = "新增用户", businessType = BusinessType.INSERT)
    public ResultVO insertSysUser(@Validated @RequestBody SysUserDTO dto) {
        return this.sysUserService.insertSysUser(dto);
    }

    @ApiOperation(value = "查看用户详情")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/detail/{id}")
    @ApiImplicitParam(value = "主键", name = "id", required = true, paramType = "path")
    public ResultVO<SysUserDetailsVO> getSysUserDetail(@PathVariable Long id) {
        return ResultVO.success(this.sysUserService.getSysUserDetail(id));
    }

    @ApiOperation(value = "编辑用户")
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/{id}")
    @ApiImplicitParam(value = "主键", name = "id", required = true, paramType = "path")
    @Log(title = "编辑用户", businessType = BusinessType.UPDATE)
    public ResultVO updateSysUserById(@PathVariable Long id, @Validated @RequestBody SysUserDTO dto) {
        return this.sysUserService.updateSysUserById(id, dto);
    }

    @ApiOperation(value = "单个用户启停用")
    @ApiImplicitParam(value = "主键", name = "id", required = true, paramType = "path")
    @Log(title = "单个用户启停用", businessType = BusinessType.UPDATE)
    @PatchMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/{id}")
    public ResultVO updateSysUserStatus(@PathVariable Long id) {
        return this.sysUserService.updateSysUserStatus(id);
    }

    @ApiOperation(value = "批量用户启停用")
    @ApiImplicitParam(value = "一批主键", name = "ids", required = true, paramType = "path")
    @Log(title = "批量用户启停用", businessType = BusinessType.UPDATE)
    @PatchMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/batch/{ids}")
    public ResultVO updateSysUserStatus(
            @NotBlank(message = "请选择启停状态") @ValidDict(parentType = DictTypeEnums.STATUS_TYPE,message = "请选择正确的启停状态")  @RequestParam String status,
            @PathVariable @Size(max = 1000, message = "一次最多启停用1000个用户！") @NotEmpty(message = "请选择要启停用的用户！") List<Long> ids) {
        return this.sysUserService.updateSysUserStatus(status, ids);
    }

    @ApiOperation(value = "删除")
    @ApiImplicitParam(value = "一批主键", name = "ids", required = true, paramType = "path")
    @Log(title = "删除", businessType = BusinessType.DELETE)
    @DeleteMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/{ids}")
    public ResultVO deleteById(@PathVariable @NotEmpty(message = "请选择要删除的用户！") @Size(max = 1000, message = "一次最多删除1000个用户！") List<Long> ids) {
        return this.sysUserService.deleteSysUserById(ids);
    }


    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(value = "主键", name = "id", required = true, paramType = "path")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PutMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/password/reset/{id}")
    public ResultVO resetPassword(@PathVariable("id") Long id) {
        return sysUserService.resetPassword(id);
    }


    @ApiOperation(value = "修改密码")
    @Log(title = "修改密码", businessType = BusinessType.UPDATE)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/password/change")
    public ResultVO changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        sysUserService.changePassword(oldPassword, newPassword);
        return ResultVO.success();
    }

    @ApiOperation(value = "查询锁定")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/removeLock")
    public ResultVO removeLock() {
        Set keys = redisService.getRedisTemplate().keys(LOGIN_LOCK_KEY + "*");
        if (StringUtils.isNotEmpty(keys)){
            return ResultVO.success(keys.stream().map(e-> e.toString().replace(LOGIN_LOCK_KEY, "")).collect(Collectors.toList()));
        }
        return ResultVO.success();
    }

    @ApiOperation(value = "清理锁定状态")
    @Log(title = "清理锁定状态", businessType = BusinessType.UPDATE)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/removeLock/{name}")
    public ResultVO removeLock(@PathVariable String name) {
        redisService.getRedisTemplate().delete(name);
        return ResultVO.success();
    }

    @ApiOperation(value = "清理锁定状态")
    @Log(title = "清理锁定状态", businessType = BusinessType.UPDATE)
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysUser/removeLock")
    public ResultVO removeAllLock() {
        Set keys = redisService.getRedisTemplate().keys(LOGIN_LOCK_KEY + "*");
        redisService.deleteObject(keys);
        return ResultVO.success();
    }
}

package com.ptidss.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户管理（DDL 10.1 sys_user + 区域授权）
 */
@RestController
@RequestMapping("/admin/users")
@RequiresPermissions("menu:admin")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String status) {
        return Result.success(sysUserService.page(pageNum, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @GetMapping("/{id}/regions")
    public Result<List<String>> regions(@PathVariable Long id) {
        return Result.success(sysUserService.regionsOf(id));
    }

    @Log(action = "user_create", targetType = "sys_user")
    @PostMapping
    @RequiresRoles("admin")
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        SysUser user = buildUser(body);
        String password = (String) body.get("password");
        @SuppressWarnings("unchecked")
        List<String> regions = (List<String>) body.get("regions");
        sysUserService.create(user, password, regions);
        return Result.success();
    }

    @Log(action = "user_update", targetType = "sys_user")
    @PutMapping
    @RequiresRoles("admin")
    public Result<Void> update(@RequestBody Map<String, Object> body) {
        SysUser user = buildUser(body);
        @SuppressWarnings("unchecked")
        List<String> regions = (List<String>) body.get("regions");
        sysUserService.update(user, regions);
        return Result.success();
    }

    @Log(action = "user_reset_password", targetType = "sys_user")
    @PutMapping("/{id}/password")
    @RequiresRoles("admin")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (StrUtils.isBlank(newPassword) || newPassword.length() < 6) {
            return Result.fail("新密码长度不能少于 6 位");
        }
        sysUserService.resetPassword(id, newPassword);
        return Result.success();
    }

    @Log(action = "user_delete", targetType = "sys_user")
    @DeleteMapping("/{id}")
    @RequiresRoles("admin")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    private SysUser buildUser(Map<String, Object> body) {
        SysUser user = new SysUser();
        Object id = body.get("id");
        if (id != null) {
            user.setId(Long.valueOf(String.valueOf(id)));
        }
        user.setUsername((String) body.get("username"));
        user.setDisplayName((String) body.get("displayName"));
        user.setOrgCode((String) body.get("orgCode"));
        user.setPhone((String) body.get("phone"));
        user.setEmail((String) body.get("email"));
        user.setStatus((String) body.get("status"));
        @SuppressWarnings("unchecked")
        List<Long> roleIds = (List<Long>) body.get("roleIds");
        user.setRoleIds(roleIds);
        return user;
    }
}

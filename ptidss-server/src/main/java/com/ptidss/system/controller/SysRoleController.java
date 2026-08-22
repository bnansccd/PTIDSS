package com.ptidss.system.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.system.domain.SysRole;
import com.ptidss.system.service.SysRoleService;
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
 * 角色管理（DDL 10.2 sys_role + sys_role_permission）
 */
@RestController
@RequestMapping("/admin/roles")
@RequiresPermissions("menu:admin")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping
    public Result<List<SysRole>> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String status) {
        return Result.success(sysRoleService.list(keyword, status));
    }

    @GetMapping("/{id}")
    public Result<SysRole> detail(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    /** 查询角色已分配权限 */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> permissions(@PathVariable Long id) {
        return Result.success(sysRoleService.permissionsOf(id));
    }

    @Log(action = "role_create", targetType = "sys_role")
    @PostMapping
    public Result<Void> create(@RequestBody SysRole role) {
        sysRoleService.create(role);
        return Result.success();
    }

    @Log(action = "role_update", targetType = "sys_role")
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.update(role);
        return Result.success();
    }

    /** 保存角色权限（全量覆盖） */
    @Log(action = "role_permission_save", targetType = "sys_role_permission")
    @PutMapping("/{id}/permissions")
    public Result<Void> savePermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        sysRoleService.savePermissions(id, body.get("permissionIds"));
        return Result.success();
    }

    @Log(action = "role_delete", targetType = "sys_role")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return Result.success();
    }
}

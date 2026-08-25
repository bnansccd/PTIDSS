package com.ptidss.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysPermission;
import com.ptidss.system.mapper.SysPermissionMapper;
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

/**
 * 权限管理（DDL 10.2 sys_permission，三级权限：menu/api/data）
 */
@RestController
@RequestMapping("/admin/permissions")
@RequiresPermissions("menu:admin")
public class SysPermissionController {

    private final SysPermissionMapper sysPermissionMapper;

    public SysPermissionController(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @GetMapping
    public Result<List<SysPermission>> list(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String resourceType) {
        List<SysPermission> list = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .and(StrUtils.isNotBlank(keyword), w -> w
                        .like(SysPermission::getPermName, keyword)
                        .or()
                        .like(SysPermission::getPermCode, keyword))
                .eq(StrUtils.isNotBlank(resourceType), SysPermission::getResourceType, resourceType)
                .orderByAsc(SysPermission::getId));
        return Result.success(list);
    }

    @Log(action = "permission_create", targetType = "sys_permission")
    @PostMapping
    @RequiresRoles("admin")
    public Result<Void> create(@RequestBody SysPermission permission) {
        normalize(permission);
        sysPermissionMapper.insert(permission);
        return Result.success();
    }

    @Log(action = "permission_update", targetType = "sys_permission")
    @PutMapping
    @RequiresRoles("admin")
    public Result<Void> update(@RequestBody SysPermission permission) {
        normalize(permission);
        sysPermissionMapper.updateById(permission);
        return Result.success();
    }

    /** DDL 10.2：resource_pattern NOT NULL；menu/data 类型无匹配模式，空值兜底为 "-"；status 兜底 active */
    private void normalize(SysPermission permission) {
        if (permission.getResourcePattern() == null || permission.getResourcePattern().trim().isEmpty()) {
            permission.setResourcePattern("-");
        }
        if (StrUtils.isBlank(permission.getStatus())) {
            permission.setStatus("active");
        }
    }

    @Log(action = "permission_delete", targetType = "sys_permission")
    @DeleteMapping("/{id}")
    @RequiresRoles("admin")
    public Result<Void> delete(@PathVariable Long id) {
        sysPermissionMapper.deleteById(id);
        return Result.success();
    }
}

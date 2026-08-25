package com.ptidss.system.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.system.domain.SysConfig;
import com.ptidss.system.service.SysConfigService;
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
 * 系统配置中心（系统管理--系统配置，DDL 17 sys_config）
 * 对标 PRD："申报段数、限价参数可配置""规则参数化配置，快速适配各省规则变化"
 * 能力：分组列表（敏感脱敏）/ 新增 / 编辑（敏感 ****** 保留原值）/ 删除（内置禁删）
 * 配置保存即生效：业务侧经 SysConfigService.getString/getInt/getBool 动态读取。
 */
@RestController
@RequestMapping("/admin/configs")
@RequiresPermissions("menu:admin")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    public SysConfigController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    /** 配置列表（可按分组/关键字过滤；敏感项脱敏 ******） */
    @GetMapping
    public Result<List<SysConfig>> list(@RequestParam(required = false) String group,
                                        @RequestParam(required = false) String keyword) {
        return Result.success(sysConfigService.list(group, keyword));
    }

    /** 配置详情（编辑回显用；敏感项 ****** 表示保留原值） */
    @GetMapping("/{id}")
    public Result<SysConfig> detail(@PathVariable Long id) {
        return Result.success(sysConfigService.getById(id));
    }

    /** 新增配置（admin；key 唯一；敏感项加密落库） */
    @Log(action = "sys_config_create", targetType = "sys_config")
    @PostMapping
    @RequiresRoles("admin")
    public Result<Void> create(@RequestBody SysConfig config) {
        sysConfigService.create(config);
        return Result.success();
    }

    /** 编辑配置（admin；内置项 key 不可改；敏感项 ****** 保留原值） */
    @Log(action = "sys_config_update", targetType = "sys_config")
    @PutMapping("/{id}")
    @RequiresRoles("admin")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysConfig config) {
        config.setId(id);
        sysConfigService.update(config);
        return Result.success();
    }

    /** 删除配置（admin；内置项禁删，可禁用） */
    @Log(action = "sys_config_delete", targetType = "sys_config")
    @DeleteMapping("/{id}")
    @RequiresRoles("admin")
    public Result<Void> delete(@PathVariable Long id) {
        sysConfigService.delete(id);
        return Result.success();
    }
}

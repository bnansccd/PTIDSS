package com.ptidss.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.system.domain.AuditLog;
import com.ptidss.system.service.AuditLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志（DDL 10.3 audit_log；等保三级审计，按省检索）
 */
@RestController
@RequestMapping("/admin/logs")
@RequiresPermissions("menu:admin")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public Result<Page<AuditLog>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String action,
                                       @RequestParam(required = false) String username,
                                       @RequestParam(required = false) String regionCode,
                                       @RequestParam(required = false) String result) {
        return Result.success(auditLogService.page(pageNum, pageSize, action, username, regionCode, result));
    }

    @GetMapping("/{id}")
    public Result<AuditLog> detail(@PathVariable Long id) {
        return Result.success(auditLogService.getById(id));
    }
}

package com.ptidss.system.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.system.domain.SysRegion;
import com.ptidss.system.service.SysRegionService;
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
 * 区域管理（多省配置化：评审决议⑤）
 */
@RestController
@RequestMapping("/admin/regions")
@RequiresPermissions("menu:admin")
public class SysRegionController {

    private final SysRegionService sysRegionService;

    public SysRegionController(SysRegionService sysRegionService) {
        this.sysRegionService = sysRegionService;
    }

    @GetMapping
    public Result<List<SysRegion>> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String status) {
        return Result.success(sysRegionService.list(keyword, status));
    }

    @GetMapping("/{id}")
    public Result<SysRegion> detail(@PathVariable Long id) {
        return Result.success(sysRegionService.getById(id));
    }

    @Log(action = "region_create", targetType = "sys_region")
    @PostMapping
    public Result<Void> create(@RequestBody SysRegion region) {
        sysRegionService.create(region);
        return Result.success();
    }

    @Log(action = "region_update", targetType = "sys_region")
    @PutMapping
    public Result<Void> update(@RequestBody SysRegion region) {
        sysRegionService.update(region);
        return Result.success();
    }

    @Log(action = "region_delete", targetType = "sys_region")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRegionService.delete(id);
        return Result.success();
    }
}

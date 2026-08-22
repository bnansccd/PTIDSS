package com.ptidss.data.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.data.service.DataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据底座（对齐 OpenAPI V1.1 /data/**；FR-PD-04 数据全流程管理 P0 / FR-PD-05 数据质量血缘 P1）
 */
@RestController
@RequestMapping("/data")
@RequiresPermissions("menu:data")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    /** 数据源台账（编码/类型/同步状态/最近运行） */
    @GetMapping("/sources")
    public Result<List<Map<String, Object>>> sources() {
        return Result.success(dataService.sources());
    }

    /** 新增数据源（台账登记；编码唯一，类型/同步模式/对接方式/状态枚举校验；仅 admin） */
    @PostMapping("/sources")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> createSource(@RequestBody Map<String, Object> body) {
        return Result.success(dataService.createSource(
                body.get("sourceCode") == null ? null : String.valueOf(body.get("sourceCode")),
                body.get("sourceType") == null ? null : String.valueOf(body.get("sourceType")),
                body.get("syncMode") == null ? null : String.valueOf(body.get("syncMode")),
                body.get("connType") == null ? null : String.valueOf(body.get("connType")),
                body.get("frequency") == null ? null : String.valueOf(body.get("frequency")),
                body.get("status") == null ? null : String.valueOf(body.get("status")),
                body.get("connectConfig") == null ? null : String.valueOf(body.get("connectConfig"))));
    }

    /** 更新数据源对接配置（连接方式/连接参数/同步模式/频率/启停；客户部署适配；仅 admin） */
    @Log(action = "data_source_update", targetType = "data_source")
    @PutMapping("/sources/{id}")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> updateSource(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return Result.success(dataService.updateSource(id,
                body.get("syncMode") == null ? null : String.valueOf(body.get("syncMode")),
                body.get("connType") == null ? null : String.valueOf(body.get("connType")),
                body.get("connectConfig") == null ? null : String.valueOf(body.get("connectConfig")),
                body.get("frequency") == null ? null : String.valueOf(body.get("frequency")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 手动触发采集任务（market/trade/settlement/weather/intel；force 强制重跑；仅 admin） */
    @PostMapping("/collect-tasks")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> collect(@RequestBody Map<String, Object> body) {
        return Result.success(dataService.collect(
                body.get("taskType") == null ? null : String.valueOf(body.get("taskType")),
                body.get("force") == null ? null : Boolean.valueOf(String.valueOf(body.get("force")))));
    }

    /** 数据质量报告（完整率/准确率/及时率） */
    @GetMapping("/quality/report")
    public Result<Map<String, Object>> qualityReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(dataService.qualityReport(startDate, endDate));
    }

    /** 数据血缘查询（nodeId 缺省返回全景） */
    @GetMapping("/lineage")
    public Result<Map<String, Object>> lineage(@RequestParam(required = false) String nodeId) {
        return Result.success(dataService.lineage(nodeId));
    }
}

package com.ptidss.intel.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.intel.domain.IntelNews;
import com.ptidss.intel.domain.IntelPushRule;
import com.ptidss.intel.service.IntelFetchService;
import com.ptidss.intel.service.IntelService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 情报中心（对齐 OpenAPI V1.1 /intel/**；FR-INT-04 情报中心 RE-01 P0）
 */
@RestController
@RequestMapping("/intel")
@RequiresPermissions("menu:intel")
public class IntelController {

    private final IntelService intelService;
    private final IntelFetchService intelFetchService;

    public IntelController(IntelService intelService, IntelFetchService intelFetchService) {
        this.intelService = intelService;
        this.intelFetchService = intelFetchService;
    }

    /** 情报流（重要度/类型筛选，分页；区域：当前省 + 全国） */
    @GetMapping("/news")
    public Result<Map<String, Object>> news(
            @RequestParam(required = false) String importance,
            @RequestParam(required = false) String intelType,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<IntelNews> p =
                intelService.listNews(importance, intelType, pageNo, pageSize).getData();
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 情报源台账（60+ 源，种子代表性子集；配置与状态；连接参数脱敏视图） */
    @GetMapping("/sources")
    public Result<List<Map<String, Object>>> sources() {
        return Result.success(intelService.listSources());
    }

    /** 行情采集状态监测（V2.5：各源最近成功/失败原因/连续失败/频率/端点域名，长期验证用） */
    @GetMapping("/fetch-status")
    public Result<List<Map<String, Object>>> fetchStatus() {
        return Result.success(intelFetchService.fetchStatus());
    }

    /** 手动触发行情采集（V2.5：force=true 忽略频率立即全量重跑；仅 admin） */
    @Log(action = "intel_fetch", targetType = "intel_source")
    @PostMapping("/fetch")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> fetch(@RequestBody(required = false) Map<String, Object> body) {
        boolean force = body != null && body.get("force") != null
                && Boolean.parseBoolean(String.valueOf(body.get("force")));
        return Result.success(intelFetchService.fetchAll(force));
    }

    /** 新增情报源（台账登记；编码唯一，类型/采集方式/对接方式/状态枚举校验；仅 admin） */
    @PostMapping("/sources")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> createSource(@RequestBody Map<String, Object> body) {
        return Result.success(intelService.createSource(
                body.get("sourceCode") == null ? null : String.valueOf(body.get("sourceCode")),
                body.get("sourceName") == null ? null : String.valueOf(body.get("sourceName")),
                body.get("intelType") == null ? null : String.valueOf(body.get("intelType")),
                body.get("fetchMode") == null ? null : String.valueOf(body.get("fetchMode")),
                body.get("connType") == null ? null : String.valueOf(body.get("connType")),
                body.get("connConfig") == null ? null : String.valueOf(body.get("connConfig")),
                body.get("frequency") == null ? null : String.valueOf(body.get("frequency")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 更新情报源对接配置（连接方式/连接参数/频率/启停；客户部署适配；仅 admin） */
    @Log(action = "intel_source_update", targetType = "intel_source")
    @PutMapping("/sources/{id}")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> updateSource(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return Result.success(intelService.updateSource(id,
                body.get("fetchMode") == null ? null : String.valueOf(body.get("fetchMode")),
                body.get("connType") == null ? null : String.valueOf(body.get("connType")),
                body.get("connConfig") == null ? null : String.valueOf(body.get("connConfig")),
                body.get("frequency") == null ? null : String.valueOf(body.get("frequency")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 删除情报源台账（软删除；历史情报保留，采集自动跳过；仅 admin） */
    @Log(action = "intel_source_delete", targetType = "intel_source")
    @DeleteMapping("/sources/{id}")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> deleteSource(@PathVariable Long id) {
        return Result.success(intelService.deleteSource(id));
    }

    /** 推送规则列表（标签×重要度→角色/渠道） */
    @GetMapping("/push-rules")
    public Result<List<IntelPushRule>> pushRules() {
        return Result.success(intelService.listPushRules());
    }

    /** 配置情报推送规则（high 级自动追加 sms/miniapp 实时渠道，≤30s 推送；仅 admin） */
    @PostMapping("/push-rules")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> createPushRule(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) body.get("matchTags");
        @SuppressWarnings("unchecked")
        List<String> targets = (List<String>) body.get("targets");
        return Result.success(intelService.createPushRule(
                body.get("ruleName") == null ? null : String.valueOf(body.get("ruleName")),
                tags,
                body.get("importance") == null ? null : String.valueOf(body.get("importance")),
                targets));
    }

    /** 手动触发推送规则执行（情报→消息中心联动；另有定时 30s 兜底；仅 admin） */
    @PostMapping("/push-rules/execute")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> executePushRules() {
        return Result.success("推送执行完成", intelService.executePushRules());
    }
}

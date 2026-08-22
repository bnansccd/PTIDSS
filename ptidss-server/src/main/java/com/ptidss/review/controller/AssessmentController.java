package com.ptidss.review.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.review.domain.AssessIndicator;
import com.ptidss.review.domain.AssessResult;
import com.ptidss.review.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易考核（对齐 OpenAPI V1.0 /assessment/**：指标体系/考核结果/申诉审核；FR-DM-07）
 * V2.4 操作友好性：指标体系支持新增/编辑/自定义（管理端）
 */
@RestController
@RequestMapping("/assessment")
@RequiresPermissions("menu:review")
public class AssessmentController {

    private final ReviewService reviewService;

    public AssessmentController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** 考核指标体系（权重/目标/评分规则） */
    @GetMapping("/indicators")
    public Result<List<AssessIndicator>> indicators() {
        return Result.success(reviewService.listIndicators());
    }

    /** 考核指标体系全量（含停用，管理端维护用） */
    @GetMapping("/indicators/all")
    @RequiresRoles("admin")
    public Result<List<AssessIndicator>> allIndicators() {
        return Result.success(reviewService.listAllIndicators());
    }

    /** 新增考核指标（编码唯一/权重 0-1；考核体系自定义；仅 admin） */
    @Log(action = "assess_indicator_create", targetType = "assess_indicator")
    @PostMapping("/indicators")
    @RequiresRoles("admin")
    public Result<AssessIndicator> createIndicator(@RequestBody Map<String, Object> body) {
        BigDecimal weight = body.get("weight") == null ? null
                : new BigDecimal(String.valueOf(body.get("weight")));
        return Result.success(reviewService.createIndicator(
                body.get("code") == null ? null : String.valueOf(body.get("code")),
                body.get("name") == null ? null : String.valueOf(body.get("name")),
                weight,
                body.get("formula") == null ? null : String.valueOf(body.get("formula")),
                body.get("targetValue") == null ? null : String.valueOf(body.get("targetValue")),
                body.get("scoringRule") == null ? null : String.valueOf(body.get("scoringRule")),
                body.get("dataSource") == null ? null : String.valueOf(body.get("dataSource")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 更新考核指标（名称/权重/目标/评分规则/启停；仅 admin） */
    @Log(action = "assess_indicator_update", targetType = "assess_indicator")
    @PutMapping("/indicators/{id}")
    @RequiresRoles("admin")
    public Result<Void> updateIndicator(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BigDecimal weight = body.get("weight") == null ? null
                : new BigDecimal(String.valueOf(body.get("weight")));
        reviewService.updateIndicator(id,
                body.get("name") == null ? null : String.valueOf(body.get("name")),
                weight,
                body.get("formula") == null ? null : String.valueOf(body.get("formula")),
                body.get("targetValue") == null ? null : String.valueOf(body.get("targetValue")),
                body.get("scoringRule") == null ? null : String.valueOf(body.get("scoringRule")),
                body.get("dataSource") == null ? null : String.valueOf(body.get("dataSource")),
                body.get("status") == null ? null : String.valueOf(body.get("status")));
        return Result.success();
    }

    /** 考核结果（周期必填/范围筛选） */
    @GetMapping("/results")
    public Result<List<AssessResult>> results(
            @RequestParam String period,
            @RequestParam(required = false) String scope) {
        return Result.success(reviewService.listResults(period, scope));
    }

    /** 提交考核申诉（进入审核流程） */
    @Log(action = "assess_appeal_create", targetType = "assess_appeal")
    @PostMapping("/appeals")
    public Result<Map<String, Object>> createAppeal(@RequestBody Map<String, Object> body) {
        Long resultId = body.get("resultId") == null
                ? null : Long.valueOf(String.valueOf(body.get("resultId")));
        String appealReason = body.get("appealReason") == null ? null : String.valueOf(body.get("appealReason"));
        List<String> evidenceUrls = body.get("evidenceUrls") == null
                ? null : (List<String>) body.get("evidenceUrls");
        return Result.success(reviewService.createAppeal(resultId, appealReason, evidenceUrls));
    }

    /** 审核申诉（批准/驳回，批准后重算结果） */
    @Log(action = "assess_appeal_process", targetType = "assess_appeal")
    @PostMapping("/appeals/{id}/process")
    public Result<Void> processAppeal(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String decision = body.get("decision") == null ? null : String.valueOf(body.get("decision"));
        String comment = body.get("comment") == null ? null : String.valueOf(body.get("comment"));
        reviewService.processAppeal(id, decision, comment);
        return Result.success();
    }
}

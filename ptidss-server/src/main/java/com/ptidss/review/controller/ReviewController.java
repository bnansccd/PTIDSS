package com.ptidss.review.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.common.utils.DateUtils;
import com.ptidss.review.domain.ReviewReport;
import com.ptidss.review.service.ReviewService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 智能复盘（对齐 OpenAPI V1.0 /review/**：复盘报告生成/三层归因/策略回流；FR-RS-01）
 */
@RestController
@RequestMapping("/review")
@RequiresPermissions("menu:review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** 生成复盘报告（周/月/专项，三层归因 + 策略评估） */
    @Log(action = "review_report_generate", targetType = "review_report")
    @PostMapping("/reports")
    public Result<Map<String, Object>> createReport(@RequestBody Map<String, Object> body) {
        String reportType = body.get("reportType") == null ? null : String.valueOf(body.get("reportType"));
        Date startDate = parseDate(body.get("startDate"));
        Date endDate = parseDate(body.get("endDate"));
        List<String> focusTopics = body.get("focusTopics") == null
                ? null : (List<String>) body.get("focusTopics");
        return Result.success(reviewService.createReport(reportType, startDate, endDate, focusTopics));
    }

    /** 复盘报告列表（按类型/周期筛选，倒序） */
    @GetMapping("/reports")
    public Result<List<Map<String, Object>>> listReports(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date periodStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date periodEnd) {
        return Result.success(reviewService.listReports(reportType, periodStart, periodEnd));
    }

    /** 复盘报告详情（三层归因/策略评估） */
    @GetMapping("/reports/{id}")
    public Result<Map<String, Object>> getReport(@PathVariable Long id) {
        return Result.success(reviewService.getReport(id));
    }

    /** 策略回流（有效/失效/调整建议，沉淀策略库） */
    @Log(action = "review_strategy_feedback", targetType = "strategy_feedback")
    @PostMapping("/strategy-feedback")
    public Result<Void> strategyFeedback(@RequestBody Map<String, Object> body) {
        String strategyCode = body.get("strategyCode") == null ? null : String.valueOf(body.get("strategyCode"));
        String feedback = body.get("feedback") == null ? null : String.valueOf(body.get("feedback"));
        Map<String, Object> updatedParams = body.get("updatedParams") == null
                ? null : (Map<String, Object>) body.get("updatedParams");
        Long reviewId = body.get("reviewId") == null
                ? null : Long.valueOf(String.valueOf(body.get("reviewId")));
        reviewService.strategyFeedback(strategyCode, feedback, updatedParams, reviewId);
        return Result.success();
    }

    private Date parseDate(Object value) {
        if (value == null) {
            return null;
        }
        Date d = DateUtils.parseLenient(String.valueOf(value));
        if (d == null) {
            throw new com.ptidss.common.exception.ServiceException("日期格式不合法（支持 yyyy-MM-dd 或时间日期）：" + value);
        }
        return d;
    }
}

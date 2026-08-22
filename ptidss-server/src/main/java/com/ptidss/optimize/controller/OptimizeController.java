package com.ptidss.optimize.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.common.utils.DateUtils;
import com.ptidss.optimize.domain.StrategyLibrary;
import com.ptidss.optimize.service.OptimizeService;
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
 * 联合优化（对齐 OpenAPI V1.1 /optimize/**；FR-TR-06 联合优化引擎 P0）
 */
@RestController
@RequestMapping("/optimize")
@RequiresPermissions("menu:optimize")
public class OptimizeController {

    private final OptimizeService optimizeService;

    public OptimizeController(OptimizeService optimizeService) {
        this.optimizeService = optimizeService;
    }

    /** 创建联合优化任务（daily/rolling_N/backtest；MILP 确定性模拟） */
    @PostMapping("/joint-tasks")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> createJointTask(@RequestBody Map<String, Object> body) {
        return Result.success(optimizeService.createJointTask(
                body.get("taskType") == null ? null : String.valueOf(body.get("taskType")),
                body.get("horizonDays") == null ? null
                        : Integer.valueOf(String.valueOf(body.get("horizonDays"))),
                body.get("scenarioCount") == null ? null
                        : Integer.valueOf(String.valueOf(body.get("scenarioCount"))),
                (Map<String, Object>) body.get("objectiveWeights"),
                (Map<String, Object>) body.get("constraints")));
    }

    /** 优化任务状态与结果摘要（expectedRevenue/CVaR/求解器/耗时） */
    @GetMapping("/joint-tasks/{taskId}")
    public Result<Map<String, Object>> jointTaskStatus(@PathVariable Long taskId) {
        return Result.success(optimizeService.jointTaskStatus(taskId));
    }

    /** 发起策略回测（策略编码 + 区间 → runId） */
    @PostMapping("/backtests")
    public Result<Map<String, Object>> backtest(@RequestBody Map<String, Object> body) {
        return Result.success(optimizeService.backtest(
                body.get("strategyCode") == null ? null : String.valueOf(body.get("strategyCode")),
                body.get("startDate") == null ? null
                        : parseDate(String.valueOf(body.get("startDate"))),
                body.get("endDate") == null ? null
                        : parseDate(String.valueOf(body.get("endDate"))),
                body.get("marketDataVersion") == null ? null
                        : String.valueOf(body.get("marketDataVersion"))));
    }

    /** 策略库列表（回测/复盘/人工三源沉淀） */
    @GetMapping("/strategies")
    public Result<List<StrategyLibrary>> strategies() {
        return Result.success(optimizeService.strategies());
    }

    private Date parseDate(String value) {
        Date d = DateUtils.parseLenient(value);
        if (d == null) {
            throw new com.ptidss.common.exception.ServiceException("日期格式不合法（支持 yyyy-MM-dd 或时间日期）：" + value);
        }
        return d;
    }
}

package com.ptidss.forecast.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.common.utils.DateUtils;
import com.ptidss.forecast.service.ForecastService;
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
 * 预测中心（对齐 OpenAPI V1.1 /forecast/**；FR-TR-01~03/06 负荷/电价/新能源预测 P0）
 */
@RestController
@RequestMapping("/forecast")
@RequiresPermissions("menu:forecast")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    /** 创建预测任务（异步模拟；电价必填 marketType，负荷/新能源必填 regionCode） */
    @PostMapping("/tasks")
    public Result<Map<String, Object>> createTask(@RequestBody Map<String, Object> body) {
        return Result.success(forecastService.createTask(
                body.get("modelCode") == null ? null : String.valueOf(body.get("modelCode")),
                body.get("predictDate") == null ? null : parseDate(String.valueOf(body.get("predictDate"))),
                body.get("marketType") == null ? null : String.valueOf(body.get("marketType")),
                body.get("regionCode") == null ? null : String.valueOf(body.get("regionCode"))));
    }

    /** 预测任务状态（status/modelVersion/elapsedMs） */
    @GetMapping("/tasks/{taskId}")
    public Result<Map<String, Object>> taskStatus(@PathVariable Long taskId) {
        return Result.success(forecastService.taskStatus(taskId));
    }

    /** 96 点预测结果（predictType/tradeDate 必填，含 90% 置信区间） */
    @GetMapping("/results")
    public Result<List<Map<String, Object>>> results(
            @RequestParam String predictType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date tradeDate,
            @RequestParam(required = false) String modelVersion) {
        return Result.success(forecastService.results(predictType, tradeDate, modelVersion));
    }

    /** 模型注册列表（与 model 域共用 model_registry） */
    @GetMapping("/models")
    public Result<List<Map<String, Object>>> models() {
        return Result.success(forecastService.models());
    }

    /** 触发模型训练（daily_increment/weekly_full） */
    @PostMapping("/models/train")
    public Result<Map<String, Object>> train(@RequestBody Map<String, Object> body) {
        return Result.success(forecastService.train(
                body.get("modelCode") == null ? null : String.valueOf(body.get("modelCode")),
                body.get("mode") == null ? null : String.valueOf(body.get("mode"))));
    }

    private Date parseDate(String value) {
        Date d = DateUtils.parseLenient(value);
        if (d == null) {
            throw new com.ptidss.common.exception.ServiceException("预测日期格式不合法（支持 yyyy-MM-dd 或时间日期）：" + value);
        }
        return d;
    }
}

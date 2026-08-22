package com.ptidss.model.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.model.service.ModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模型平台（对齐 OpenAPI V1.1 /model/**；FR-PD-03 模型管理与服务化）
 * V2.4 任务报告：训练触发/离线评估/在线推理 → 详细报告和过程，与前面任务对标
 */
@RestController
@RequestMapping("/model")
@RequiresPermissions("menu:model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    /** 模型注册表（版本/指标/状态） */
    @GetMapping("/registry")
    public Result<List<Map<String, Object>>> registry() {
        return Result.success(modelService.registry());
    }

    /** 在线推理（LLM 问答/预测增强） */
    @PostMapping("/inference")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> inference(@RequestBody Map<String, Object> body) {
        return Result.success(modelService.inference(
                body.get("modelCode") == null ? null : String.valueOf(body.get("modelCode")),
                (Map<String, Object>) body.get("input"),
                body.get("temperature") == null ? null
                        : Double.valueOf(String.valueOf(body.get("temperature")))));
    }

    /** 离线评估（MAPE/方向准确率，双指标判定） */
    @PostMapping("/evaluate")
    public Result<Map<String, Object>> evaluate(@RequestBody Map<String, Object> body) {
        return Result.success(modelService.evaluate(
                body.get("modelVersion") == null ? null : String.valueOf(body.get("modelVersion")),
                body.get("testSetVersion") == null ? null : String.valueOf(body.get("testSetVersion"))));
    }

    /** V2.4 执行模型任务（train/evaluate/inference）并生成详细报告（过程步骤/结果/与前面对标） */
    @Log(action = "model_task_run", targetType = "model_task")
    @PostMapping("/tasks/{taskType}")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> runTask(@PathVariable String taskType,
                                               @RequestBody Map<String, Object> body) {
        return Result.success(modelService.runTask(
                taskType,
                body.get("modelCode") == null ? null : String.valueOf(body.get("modelCode")),
                body.get("modelVersion") == null ? null : String.valueOf(body.get("modelVersion")),
                body.get("testSetVersion") == null ? null : String.valueOf(body.get("testSetVersion")),
                body.get("mode") == null ? null : String.valueOf(body.get("mode")),
                (Map<String, Object>) body.get("input")));
    }

    /** V2.4 模型任务列表（按类型过滤；倒序） */
    @GetMapping("/tasks")
    public Result<List<Map<String, Object>>> listTasks(@RequestParam(required = false) String taskType,
                                                       @RequestParam(defaultValue = "20") int limit) {
        return Result.success(modelService.listTasks(taskType, limit));
    }

    /** V2.4 模型任务详情（输入/过程步骤/结果/对标，完整报告） */
    @GetMapping("/tasks/{id}")
    public Result<Map<String, Object>> taskDetail(@PathVariable Long id) {
        return Result.success(modelService.taskDetail(id));
    }
}

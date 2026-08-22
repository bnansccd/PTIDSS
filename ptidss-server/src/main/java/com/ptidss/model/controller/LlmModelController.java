package com.ptidss.model.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.model.domain.LlmModel;
import com.ptidss.model.service.LlmModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * LLM 模型配置（V2.2 产品化：智能体可关联的生成式模型，对接方式/温度/限额可配置）
 */
@RestController
@RequestMapping("/llm")
@RequiresPermissions("menu:model")
public class LlmModelController {

    private final LlmModelService llmModelService;

    public LlmModelController(LlmModelService llmModelService) {
        this.llmModelService = llmModelService;
    }

    /** LLM 模型列表（模型平台-LLM 管理；含 provider/endpoint/状态） */
    @GetMapping("/models")
    public Result<List<Map<String, Object>>> models() {
        return Result.success(llmModelService.listModels());
    }

    /** 新增 LLM 模型（编码唯一；提供商枚举校验；仅 admin） */
    @Log(action = "llm_model_create", targetType = "llm_model")
    @PostMapping("/models")
    @RequiresRoles("admin")
    public Result<LlmModel> create(@RequestBody Map<String, Object> body) {
        return Result.success(llmModelService.createModel(
                body.get("modelCode") == null ? null : String.valueOf(body.get("modelCode")),
                body.get("modelName") == null ? null : String.valueOf(body.get("modelName")),
                body.get("provider") == null ? null : String.valueOf(body.get("provider")),
                body.get("endpoint") == null ? null : String.valueOf(body.get("endpoint")),
                body.get("baseModel") == null ? null : String.valueOf(body.get("baseModel")),
                body.get("temperature") == null ? null : new BigDecimal(String.valueOf(body.get("temperature"))),
                body.get("maxTokens") == null ? null : Integer.valueOf(String.valueOf(body.get("maxTokens"))),
                body.get("apiKeyRef") == null ? null : String.valueOf(body.get("apiKeyRef")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 更新 LLM 模型配置（连接参数/温度/限额/启停；客户部署适配；仅 admin） */
    @Log(action = "llm_model_update", targetType = "llm_model")
    @PutMapping("/models/{id}")
    @RequiresRoles("admin")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        llmModelService.updateModel(id,
                body.get("modelName") == null ? null : String.valueOf(body.get("modelName")),
                body.get("provider") == null ? null : String.valueOf(body.get("provider")),
                body.get("endpoint") == null ? null : String.valueOf(body.get("endpoint")),
                body.get("baseModel") == null ? null : String.valueOf(body.get("baseModel")),
                body.get("temperature") == null ? null : new BigDecimal(String.valueOf(body.get("temperature"))),
                body.get("maxTokens") == null ? null : Integer.valueOf(String.valueOf(body.get("maxTokens"))),
                body.get("apiKeyRef") == null ? null : String.valueOf(body.get("apiKeyRef")),
                body.get("status") == null ? null : String.valueOf(body.get("status")));
        return Result.success();
    }
}

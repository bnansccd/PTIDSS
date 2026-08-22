package com.ptidss.agent.controller;

import com.ptidss.agent.service.AgentService;
import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
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
 * 智能体管理（对齐 OpenAPI V1.0 /agent/**；PRD FR-TR-05 智能体管理：运行监控/效果评估/参数调优）
 */
@RestController
@RequestMapping("/agent")
@RequiresPermissions("menu:decision")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /** 智能体注册表（七大智能体版本化；输入输出契约） */
    @GetMapping("/registry")
    public Result<List<Map<String, Object>>> registry() {
        return Result.success(agentService.registry());
    }

    /** 运行记录（可按智能体/会话过滤；最近 N 条） */
    @GetMapping("/runs")
    public Result<List<Map<String, Object>>> runs(@RequestParam(required = false) String agentCode,
                                                  @RequestParam(required = false) String sessionId,
                                                  @RequestParam(required = false) Integer limit) {
        return Result.success(agentService.runs(agentCode, sessionId, limit));
    }

    /** 效果评估（成功率/平均置信度/平均耗时，按智能体聚合） */
    @GetMapping("/metrics")
    public Result<List<Map<String, Object>>> metrics() {
        return Result.success(agentService.metrics());
    }

    /** 启停维护（active/disabled/maintenance） */
    @Log(action = "agent_status_update", targetType = "agent_registry")
    @PostMapping("/registry/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        agentService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /** 模型绑定（model_config.modelCode → model_registry / llmCode → llm_model；编排走推理+LLM 解读，PRD FR-TR-05） */
    @Log(action = "agent_model_bind", targetType = "agent_registry")
    @PostMapping("/registry/{id}/model-config")
    public Result<Void> bindModel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        agentService.bindModel(id, body.get("modelCode"), body.get("llmCode"));
        return Result.success();
    }
}

package com.ptidss.flow.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.flow.service.FlowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 审批流（对齐 OpenAPI V1.1 /flow/**；平台服务，M7 移动端审批依赖）
 * V2.2：流程定义可配置（环节/角色/用户），实例按定义推进（approve/reject）
 */
@RestController
@RequestMapping("/flow")
@RequiresPermissions("menu:flow")
public class FlowController {

    private final FlowService flowService;

    public FlowController(FlowService flowService) {
        this.flowService = flowService;
    }

    /** 发起流程实例（processKey + bizId + variables → instanceId） */
    @PostMapping("/start")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> start(@RequestBody Map<String, Object> body) {
        return Result.success(flowService.start(
                body.get("processKey") == null ? null : String.valueOf(body.get("processKey")),
                body.get("bizId") == null ? null : String.valueOf(body.get("bizId")),
                (Map<String, Object>) body.get("variables")));
    }

    /** 流程实例列表（M7 移动端：scope=todo 我的待办 / started 我发起的 / all 全部；分页倒序） */
    @GetMapping("/instances")
    public Result<Map<String, Object>> instances(
            @RequestParam(required = false, defaultValue = "todo") String scope,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(flowService.listInstances(scope, status, pageNo, pageSize));
    }

    /** 流程实例详情（status/currentNode/currentTasks/definitionSteps/actions） */
    @GetMapping("/instances/{instanceId}")
    public Result<Map<String, Object>> instanceDetail(@PathVariable Long instanceId) {
        return Result.success(flowService.instanceDetail(instanceId));
    }

    /** 环节推进：approve → 下一环节/完成；reject → 终止（审批留痕） */
    @Log(action = "flow_instance_advance", targetType = "flow_instance")
    @PostMapping("/instances/{instanceId}/advance")
    public Result<Map<String, Object>> advance(@PathVariable Long instanceId,
                                               @RequestBody Map<String, Object> body) {
        return Result.success(flowService.advance(instanceId,
                body.get("action") == null ? null : String.valueOf(body.get("action")),
                body.get("comment") == null ? null : String.valueOf(body.get("comment"))));
    }

    /** 业务类型字典（发起流程页下拉：编码+名称+自动单号前缀） */
    @GetMapping("/biz-types")
    public Result<List<Map<String, Object>>> bizTypes() {
        return Result.success(flowService.bizTypes());
    }

    /** 业务单号选项（按业务类型引入已有单号或自动生成规则；发起流程前置选择） */
    @GetMapping("/biz-options")
    public Result<Map<String, Object>> bizOptions(String bizType) {
        return Result.success(flowService.bizOptions(bizType));
    }

    /** 流程定义列表（环节/角色/用户/启停；审批流管理页） */
    @GetMapping("/definitions")
    public Result<List<Map<String, Object>>> definitions() {
        return Result.success(flowService.definitions());
    }

    /** 新增流程定义（客户自定义流程与环节；仅 admin） */
    @Log(action = "flow_definition_create", targetType = "flow_definition")
    @PostMapping("/definitions")
    @RequiresRoles("admin")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> createDefinition(@RequestBody Map<String, Object> body) {
        return Result.success(flowService.createDefinition(
                body.get("processKey") == null ? null : String.valueOf(body.get("processKey")),
                body.get("processName") == null ? null : String.valueOf(body.get("processName")),
                body.get("bizType") == null ? null : String.valueOf(body.get("bizType")),
                (List<Map<String, Object>>) body.get("steps")));
    }

    /** 更新流程定义（环节/角色/用户调整、启停切换；仅 admin） */
    @Log(action = "flow_definition_update", targetType = "flow_definition")
    @PutMapping("/definitions/{id}")
    @RequiresRoles("admin")
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> updateDefinition(@PathVariable Long id,
                                                        @RequestBody Map<String, Object> body) {
        return Result.success(flowService.updateDefinition(id,
                body.get("processName") == null ? null : String.valueOf(body.get("processName")),
                (List<Map<String, Object>>) body.get("steps"),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }
}

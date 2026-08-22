package com.ptidss.decision.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.decision.dto.DecisionModifyRequest;
import com.ptidss.decision.dto.DecisionRejectRequest;
import com.ptidss.decision.dto.DecisionSessionRequest;
import com.ptidss.decision.dto.DecisionSessionView;
import com.ptidss.decision.service.DecisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 辅助决策（对齐 OpenAPI V1.0 /decision/**：会话编排/人机确认/修改依据/依据链回溯）
 */
@RestController
@RequestMapping("/decision")
@RequiresPermissions("menu:decision")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /** 发起决策会话（编排异步执行；本实现同步完成） */
    @Log(action = "decision_session_create", targetType = "decision_session")
    @PostMapping("/sessions")
    public Result<Map<String, Object>> createSession(@RequestBody DecisionSessionRequest req) {
        DecisionSessionView view = decisionService.createSession(req);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("sessionId", view.getSessionId());
        resp.put("status", "completed");
        return Result.success(resp);
    }

    /** 决策会话列表（M7 移动端策略确认入口：按人审状态筛选，分页倒序） */
    @GetMapping("/sessions")
    public Result<Map<String, Object>> sessions(
            @RequestParam(required = false) String humanReviewStatus,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(decisionService.listSessions(humanReviewStatus, pageNo, pageSize));
    }

    /** 会话详情（策略/状态/阶段） */
    @GetMapping("/sessions/{sessionId}")
    public Result<DecisionSessionView> sessionDetail(@PathVariable Long sessionId) {
        return Result.success(decisionService.getSession(sessionId));
    }

    /** 交易员确认策略（写入申报准备） */
    @Log(action = "decision_session_confirm", targetType = "decision_session")
    @PostMapping("/sessions/{sessionId}/confirm")
    public Result<Void> confirm(@PathVariable Long sessionId) {
        decisionService.confirmSession(sessionId);
        return Result.success();
    }

    /** 交易员修改策略（必须记录修改依据，双人复核） */
    @Log(action = "decision_session_modify", targetType = "decision_session")
    @PostMapping("/sessions/{sessionId}/modify")
    public Result<Void> modify(@PathVariable Long sessionId, @RequestBody DecisionModifyRequest req) {
        decisionService.modifySession(sessionId, req);
        return Result.success();
    }

    /** 交易员驳回策略（必须记录原因，FR-DM-05；pending/confirmed → rejected） */
    @Log(action = "decision_session_reject", targetType = "decision_session")
    @PostMapping("/sessions/{sessionId}/reject")
    public Result<Void> reject(@PathVariable Long sessionId, @RequestBody DecisionRejectRequest req) {
        decisionService.rejectSession(sessionId, req.getReason());
        return Result.success();
    }

    /** 降级补跑（SRS FR-DM-01 R1）：对 degraded 会话重新编排，补齐缺失智能体 */
    @Log(action = "decision_session_rerun", targetType = "decision_session")
    @PostMapping("/sessions/{sessionId}/rerun")
    public Result<DecisionSessionView> rerun(@PathVariable Long sessionId) {
        return Result.success(decisionService.rerunSession(sessionId));
    }

    /** 情报触发式重算（FR-INT-04 深化）：按最新情报流重评会话情报评分快照（仅 pending） */
    @Log(action = "decision_session_intel_reassess", targetType = "decision_session")
    @PostMapping("/sessions/{sessionId}/intel-reassess")
    public Result<DecisionSessionView> intelReassess(@PathVariable Long sessionId) {
        return Result.success(decisionService.reassessIntel(sessionId));
    }

    /** 依据链全量回溯（Agent 输入/输出/冲突仲裁） */
    @GetMapping("/sessions/{sessionId}/evidence")
    public Result<Map<String, Object>> evidence(@PathVariable Long sessionId) {
        return Result.success(decisionService.getEvidence(sessionId));
    }
}

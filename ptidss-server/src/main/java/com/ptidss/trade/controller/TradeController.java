package com.ptidss.trade.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.trade.domain.Declaration;
import com.ptidss.trade.domain.RollingPlan;
import com.ptidss.trade.domain.TradeResult;
import com.ptidss.trade.dto.TradeDeclarationRequest;
import com.ptidss.trade.dto.TradeDeclarationResponse;
import com.ptidss.trade.service.TradeGatewayService;
import com.ptidss.trade.service.TradeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易申报（对齐 OpenAPI V1.0 /trade/**：日滚动方案/申报单/成交结果/持仓）
 */
@RestController
@RequestMapping("/trade")
@RequiresPermissions("menu:trade")
public class TradeController {

    private final TradeService tradeService;
    private final TradeGatewayService tradeGatewayService;

    public TradeController(TradeService tradeService, TradeGatewayService tradeGatewayService) {
        this.tradeService = tradeService;
        this.tradeGatewayService = tradeGatewayService;
    }

    /** 日滚动方案列表（多情景） */
    @GetMapping("/rolling-plans")
    public Result<List<RollingPlan>> rollingPlans(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date tradeDate,
            @RequestParam(required = false) String scenario,
            @RequestParam(required = false) String status) {
        return Result.success(tradeService.listRollingPlans(tradeDate, scenario, status));
    }

    /** 确认日滚动方案（进入申报） */
    @Log(action = "rolling_plan_confirm", targetType = "rolling_plan")
    @PostMapping("/rolling-plans/{planId}/confirm")
    public Result<Void> confirmRollingPlan(@PathVariable Long planId) {
        tradeService.confirmRollingPlan(planId);
        return Result.success();
    }

    /** 申报单列表（状态/日期筛选，对齐契约 PageResult） */
    @GetMapping("/declarations")
    public Result<Map<String, Object>> declarations(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date tradeDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<Declaration>> r =
                tradeService.listDeclarations(tradeDate, status, pageNo, pageSize);
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("list", r.getData().getRecords());
        page.put("pageNo", r.getData().getCurrent());
        page.put("pageSize", r.getData().getSize());
        page.put("total", r.getData().getTotal());
        return Result.success(page);
    }

    /** 创建申报单（含合规预检） */
    @Log(action = "declaration_create", targetType = "declaration")
    @PostMapping("/declarations")
    public Result<TradeDeclarationResponse> createDeclaration(@RequestBody TradeDeclarationRequest req) {
        return Result.success(tradeService.createDeclaration(req));
    }

    /** 申报单详情（V2.4：明细/合规预检/网关推送状态，供编辑与状态监测） */
    @GetMapping("/declarations/{id}")
    public Result<Map<String, Object>> declarationDetail(@PathVariable Long id) {
        return Result.success(tradeService.declarationDetail(id));
    }

    /** 编辑申报单（V2.4：draft/pending_submit 可编辑市场/阶段/明细，重新合规预检） */
    @Log(action = "declaration_update", targetType = "declaration")
    @PutMapping("/declarations/{id}")
    public Result<TradeDeclarationResponse> updateDeclaration(@PathVariable Long id,
                                                              @RequestBody TradeDeclarationRequest req) {
        return Result.success(tradeService.updateDeclaration(id, req));
    }

    /** 提交申报至交易中心（V2.4：网关推送状态监测） */
    @Log(action = "declaration_submit", targetType = "declaration")
    @PostMapping("/declarations/{id}/submit")
    public Result<Map<String, Object>> submitDeclaration(@PathVariable Long id) {
        return Result.success(tradeService.submitDeclaration(id));
    }

    // ---------- 交易网关配置（V2.4：URL/账户/密码图形化对接 + 状态监测） ----------

    /** 当前区域网关配置（敏感字段脱敏；无配置返回 null；涉及连接信息泄露面，仅 admin） */
    @GetMapping("/gateway/config")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> gatewayConfig() {
        return Result.success(tradeGatewayService.getConfig());
    }

    /** 保存网关配置（region_code 唯一 upsert；appSecret 加密落库；含密钥类敏感配置仅 admin，V3.1 对齐 V2.4 收紧） */
    @Log(action = "trade_gateway_save", targetType = "trade_gateway_config")
    @PutMapping("/gateway/config")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> saveGatewayConfig(@RequestBody Map<String, Object> body) {
        return Result.success(tradeGatewayService.saveConfig(
                body.get("gatewayName") == null ? null : String.valueOf(body.get("gatewayName")),
                body.get("endpoint") == null ? null : String.valueOf(body.get("endpoint")),
                body.get("appKey") == null ? null : String.valueOf(body.get("appKey")),
                body.get("appSecret") == null ? null : String.valueOf(body.get("appSecret")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 网关连通性测试（记录延迟/结果；涉及密钥发起外部请求，仅 admin） */
    @Log(action = "trade_gateway_test", targetType = "trade_gateway_config")
    @PostMapping("/gateway/test")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> testGateway() {
        return Result.success(tradeGatewayService.testConnection());
    }

    /** 成交结果查询（按日/市场） */
    @GetMapping("/results")
    public Result<List<TradeResult>> results(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date tradeDate,
            @RequestParam(required = false) String marketType) {
        return Result.success(tradeService.listResults(tradeDate, marketType));
    }

    /** 持仓曲线（中长期+现货合成视图） */
    @GetMapping("/positions")
    public Result<Map<String, Object>> positions(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm:ss"}) Date tradeDate) {
        return Result.success(tradeService.getPositions(tradeDate));
    }
}

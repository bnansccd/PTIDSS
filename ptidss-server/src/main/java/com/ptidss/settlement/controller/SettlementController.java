package com.ptidss.settlement.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.settlement.domain.SettlementRecord;
import com.ptidss.settlement.domain.SettlementTicket;
import com.ptidss.settlement.service.SettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 结算管理（对齐 OpenAPI V1.0 /settlement/**：结算记录/核对引擎/差异工单；FR-RS-02）
 */
@RestController
@RequestMapping("/settlement")
@RequiresPermissions("menu:settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /** 结算记录列表（周期必填/来源筛选/分页） */
    @GetMapping("/records")
    public Result<Map<String, Object>> records(
            @RequestParam String period,
            @RequestParam(required = false) String source,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SettlementRecord> p =
                settlementService.listRecords(period, source, pageNo, pageSize).getData();
        Map<String, Object> page = new java.util.LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 发起结算核对（自动比对，差异 ≥95% 通过；差异项自动生成工单） */
    @Log(action = "settlement_reconcile", targetType = "settlement_record")
    @PostMapping("/records/{id}/reconcile")
    public Result<Map<String, Object>> reconcile(@PathVariable Long id) {
        return Result.success(settlementService.reconcile(id));
    }

    /** 差异工单列表（状态筛选/分页） */
    @GetMapping("/tickets")
    public Result<Map<String, Object>> tickets(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SettlementTicket> p =
                settlementService.listTickets(status, pageNo, pageSize).getData();
        Map<String, Object> page = new java.util.LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 处理差异工单（assign/process/review/close，流程留痕） */
    @Log(action = "settlement_ticket_process", targetType = "settlement_ticket")
    @PostMapping("/tickets/{id}/process")
    public Result<Void> processTicket(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        settlementService.processTicket(id,
                String.valueOf(body.getOrDefault("action", "")),
                body.get("handler") == null ? null : String.valueOf(body.get("handler")),
                body.get("comment") == null ? null : String.valueOf(body.get("comment")));
        return Result.success();
    }
}

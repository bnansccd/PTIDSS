package com.ptidss.settlement.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.settlement.domain.OcrTask;
import com.ptidss.settlement.service.SettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 结算单 OCR 识别（对齐 OpenAPI V1.1 /ocr/**；FR-DM-03 结算单智能识别，低置信人工复核）
 */
@RestController
@RequestMapping("/ocr")
@RequiresPermissions("menu:settlement")
public class OcrController {

    private final SettlementService settlementService;

    public OcrController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /** 上传结算单图片发起识别（multipart；缺省模板自动匹配） */
    @PostMapping("/tasks")
    public Result<Map<String, Object>> createTask(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(required = false) Long templateId) {
        return Result.success(settlementService.createOcrTask(file, templateId));
    }

    /** 识别结果与置信度（低置信进入人工复核） */
    @GetMapping("/tasks/{taskId}")
    public Result<Map<String, Object>> getTask(@PathVariable Long taskId) {
        return Result.success(settlementService.getOcrTask(taskId));
    }

    /** OCR 任务列表（状态/复核状态筛选，分页；复核工作台） */
    @GetMapping("/tasks")
    public Result<Map<String, Object>> tasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OcrTask> p =
                settlementService.listOcrTasks(status, reviewStatus, pageNo, pageSize).getData();
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 人工复核提交（approved=true 确认通过 / false 修正字段；结果全留痕） */
    @PostMapping("/tasks/{taskId}/review")
    public Result<Map<String, Object>> reviewTask(@PathVariable Long taskId,
                                                  @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        Map<String, Object> fields = (Map<String, Object>) body.get("fields");
        return Result.success(settlementService.reviewOcrTask(taskId,
                Boolean.TRUE.equals(body.get("approved")),
                fields,
                body.get("comment") == null ? null : String.valueOf(body.get("comment"))));
    }
}

package com.ptidss.report.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.report.domain.ReportInstance;
import com.ptidss.report.domain.ReportTemplate;
import com.ptidss.report.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 报表引擎（对齐 OpenAPI V1.1 /report/**；FR-DM-02 报表自动生成与导出）
 * V2.4 操作友好性：报表模板可新增/编辑/自定义（管理端）；生成实例可选模板+时间周期
 */
@RestController
@RequestMapping("/report")
@RequiresPermissions("menu:report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** 报表模板列表（含口径说明） */
    @GetMapping("/templates")
    public Result<List<ReportTemplate>> listTemplates() {
        return Result.success(reportService.listTemplates());
    }

    /** 报表模板全量（含停用，管理端维护用） */
    @GetMapping("/templates/all")
    @RequiresRoles("admin")
    public Result<List<ReportTemplate>> allTemplates() {
        return Result.success(reportService.listAllTemplates());
    }

    /** 新增报表模板（编码唯一；指标/布局/表头 JSON 配置；仅 admin） */
    @Log(action = "report_template_create", targetType = "report_template")
    @PostMapping("/templates")
    @RequiresRoles("admin")
    public Result<ReportTemplate> createTemplate(@RequestBody Map<String, Object> body) {
        return Result.success(reportService.createTemplate(
                body.get("code") == null ? null : String.valueOf(body.get("code")),
                body.get("name") == null ? null : String.valueOf(body.get("name")),
                body.get("type") == null ? null : String.valueOf(body.get("type")),
                body.get("periodType") == null ? null : String.valueOf(body.get("periodType")),
                body.get("datasourceConfig") == null ? null : String.valueOf(body.get("datasourceConfig")),
                body.get("layout") == null ? null : String.valueOf(body.get("layout")),
                body.get("headerConfig") == null ? null : String.valueOf(body.get("headerConfig")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 更新报表模板（名称/指标/布局/表头口径/启停；仅 admin） */
    @Log(action = "report_template_update", targetType = "report_template")
    @PutMapping("/templates/{id}")
    @RequiresRoles("admin")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        reportService.updateTemplate(id,
                body.get("name") == null ? null : String.valueOf(body.get("name")),
                body.get("type") == null ? null : String.valueOf(body.get("type")),
                body.get("periodType") == null ? null : String.valueOf(body.get("periodType")),
                body.get("datasourceConfig") == null ? null : String.valueOf(body.get("datasourceConfig")),
                body.get("layout") == null ? null : String.valueOf(body.get("layout")),
                body.get("headerConfig") == null ? null : String.valueOf(body.get("headerConfig")),
                body.get("status") == null ? null : String.valueOf(body.get("status")));
        return Result.success();
    }

    /** 报表实例列表（按区域隔离） */
    @GetMapping("/instances")
    public Result<List<ReportInstance>> listInstances(@RequestParam(required = false) String period) {
        return Result.success(reportService.listInstances(period));
    }

    /** 生成报表实例（模板+周期+格式） */
    @PostMapping("/instances")
    public Result<Map<String, Object>> createInstance(@RequestBody Map<String, Object> body) {
        String format = body.get("format") == null ? "excel" : String.valueOf(body.get("format"));
        return Result.success(reportService.createInstance(
                String.valueOf(body.get("templateCode")),
                String.valueOf(body.get("period")),
                format));
    }

    /** 下载报表文件（CSV 文本流，报送格式：表头+口径说明+指标行） */
    @GetMapping("/instances/{id}/export")
    public void export(@PathVariable Long id, HttpServletResponse response) {
        byte[] content = reportService.exportInstance(id);
        try {
            String fileName = URLEncoder.encode("ptidss_report_" + id + ".csv", StandardCharsets.UTF_8.name());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new com.ptidss.common.exception.ServiceException("报表导出失败");
        }
    }
}

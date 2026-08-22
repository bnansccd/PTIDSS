package com.ptidss.policy.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.policy.domain.PolicyDocument;
import com.ptidss.policy.service.PolicyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 政策研判（对齐 OpenAPI V1.1 /policy/**；FR-PD-01 P0：政策库/智能解析/影响研判/简报导出）
 */
@RestController
@RequestMapping("/policy")
@RequiresPermissions("menu:policy")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /** 政策文件分页列表（分类/关键词/状态多维筛选） */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PolicyDocument> p =
                policyService.list(category, keyword, status, pageNo, pageSize).getData();
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 上传/新建政策文档（登记入政策库；fileUrl 缺省模拟 MinIO 对象地址；仅 admin） */
    @PostMapping("/upload")
    @RequiresRoles("admin")
    public Result<PolicyDocument> upload(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) body.get("tags");
        return Result.success(policyService.upload(
                body.get("title") == null ? null : String.valueOf(body.get("title")),
                body.get("issuingBody") == null ? null : String.valueOf(body.get("issuingBody")),
                body.get("category") == null ? null : String.valueOf(body.get("category")),
                tags,
                body.get("publishDate") == null ? null : String.valueOf(body.get("publishDate")),
                body.get("effectiveDate") == null ? null : String.valueOf(body.get("effectiveDate")),
                body.get("status") == null ? null : String.valueOf(body.get("status")),
                body.get("fileUrl") == null ? null : String.valueOf(body.get("fileUrl"))));
    }

    /** 上传新政策（multipart：政策原文文件 + 登记信息，文件落盘本地存储；仅 admin；操作友好性：直接选择政策文件上传） */
    @PostMapping("/upload-file")
    @RequiresRoles("admin")
    public Result<PolicyDocument> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam String title,
                                             @RequestParam(required = false) String issuingBody,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(required = false) String tags,
                                             @RequestParam(required = false) String publishDate,
                                             @RequestParam(required = false) String effectiveDate,
                                             @RequestParam(required = false) String status) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("政策文件不能为空");
        }
        List<String> tagList = StrUtils.isBlank(tags) ? new java.util.ArrayList<>()
                : java.util.Arrays.asList(tags.split("[,，]"));
        try {
            return Result.success(policyService.uploadFile(title, issuingBody,
                    StrUtils.isBlank(category) ? "provincial" : category, tagList,
                    publishDate, effectiveDate, status, file.getOriginalFilename(), file.getBytes()));
        } catch (IOException e) {
            throw new ServiceException("政策文件读取失败：" + e.getMessage());
        }
    }

    /** 政策原文文件下载（fileUrl=local:// 本地落盘文件；MinIO 模拟地址或文件缺失返回 404） */
    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> file(@PathVariable Long id) {
        Map<String, Object> payload = policyService.readFile(id);
        if (payload == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] data = (byte[]) payload.get("data");
        String filename;
        try {
            filename = java.net.URLEncoder.encode(
                    "policy_" + id + "_" + payload.get("name"), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            filename = "policy_" + id;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + filename)
                .contentType(new MediaType("application", "octet-stream", StandardCharsets.UTF_8))
                .body(data);
    }

    /** 政策详情（含解析条款/影响研判/沉淀规则） */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(policyService.detail(id));
    }

    /** 解析政策文档（LLM 抽取条款→结构化规则候选；异步任务返回统计） */
    @PostMapping("/parse")
    public Result<Map<String, Object>> parse(@RequestBody Map<String, Object> body) {
        Long policyId = body.get("policyId") == null ? null
                : Long.valueOf(String.valueOf(body.get("policyId")));
        boolean reparse = body.get("reparse") != null
                && Boolean.parseBoolean(String.valueOf(body.get("reparse")));
        if (policyId == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", "policyId 不能为空");
            return Result.fail("policyId 不能为空");
        }
        return Result.success(policyService.parse(policyId, reparse));
    }

    /** 政策研判简报导出（application/octet-stream） */
    @GetMapping("/{id}/brief")
    public ResponseEntity<byte[]> brief(@PathVariable Long id) {
        byte[] data = policyService.brief(id);
        String filename = "policy_brief_" + id + "_"
                + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + filename)
                .contentType(new MediaType("application", "octet-stream", StandardCharsets.UTF_8))
                .body(data);
    }
}

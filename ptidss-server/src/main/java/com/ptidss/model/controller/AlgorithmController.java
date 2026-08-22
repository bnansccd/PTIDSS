package com.ptidss.model.controller;

import com.ptidss.common.annotation.Log;
import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.annotation.RequiresRoles;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.model.domain.AlgorithmRegistry;
import com.ptidss.model.service.AlgorithmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 算法注册表（V2.2 产品化：专业算法注册/替换/匹配；决策过程按类目应用并标注版本）
 */
@RestController
@RequestMapping("/algorithm")
@RequiresPermissions("menu:model")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /** 算法注册表（按类目/状态筛选；模型平台-算法管理） */
    @GetMapping("/registry")
    public Result<List<Map<String, Object>>> registry(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return Result.success(algorithmService.listAlgorithms(category, status));
    }

    /** SPI 执行器清单（P3 插件化执行：算法注册可绑定执行器，空=按类目默认） */
    @GetMapping("/spis")
    public Result<List<Map<String, Object>>> spis() {
        return Result.success(algorithmService.listSpis());
    }

    /** 算法文件自动解析（操作友好性：客户上传专业算法文件，系统自动解析类目/参数/说明并回填注册表单；仅 admin） */
    @PostMapping("/parse-file")
    @RequiresRoles("admin")
    public Result<Map<String, Object>> parseFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(required = false) String category) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("算法文件不能为空");
        }
        try {
            return Result.success(algorithmService.parseAlgorithmFile(
                    file.getOriginalFilename(), file.getBytes(), file.getSize(), category));
        } catch (IOException e) {
            throw new ServiceException("算法文件读取失败：" + e.getMessage());
        }
    }

    /** 新增算法（编码+版本唯一；类目枚举校验；spiKey 可选；仅 admin） */
    @Log(action = "algorithm_create", targetType = "algorithm_registry")
    @PostMapping("/registry")
    @RequiresRoles("admin")
    public Result<AlgorithmRegistry> create(@RequestBody Map<String, Object> body) {
        return Result.success(algorithmService.createAlgorithm(
                body.get("algCode") == null ? null : String.valueOf(body.get("algCode")),
                body.get("algName") == null ? null : String.valueOf(body.get("algName")),
                body.get("category") == null ? null : String.valueOf(body.get("category")),
                body.get("description") == null ? null : String.valueOf(body.get("description")),
                body.get("paramsSchema") == null ? null : String.valueOf(body.get("paramsSchema")),
                body.get("version") == null ? null : String.valueOf(body.get("version")),
                body.get("spiKey") == null ? null : String.valueOf(body.get("spiKey")),
                body.get("status") == null ? null : String.valueOf(body.get("status"))));
    }

    /** 更新算法（参数模板/说明/版本/SPI 执行器/启停；替换算法 = 新版本启用 + 旧版停用；仅 admin） */
    @Log(action = "algorithm_update", targetType = "algorithm_registry")
    @PutMapping("/registry/{id}")
    @RequiresRoles("admin")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        algorithmService.updateAlgorithm(id,
                body.get("algName") == null ? null : String.valueOf(body.get("algName")),
                body.get("description") == null ? null : String.valueOf(body.get("description")),
                body.get("paramsSchema") == null ? null : String.valueOf(body.get("paramsSchema")),
                body.get("version") == null ? null : String.valueOf(body.get("version")),
                body.get("spiKey") == null ? null : String.valueOf(body.get("spiKey")),
                body.get("status") == null ? null : String.valueOf(body.get("status")));
        return Result.success();
    }
}

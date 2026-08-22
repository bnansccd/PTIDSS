package com.ptidss.model.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.model.domain.AlgorithmRegistry;
import com.ptidss.model.mapper.AlgorithmRegistryMapper;
import com.ptidss.model.spi.AlgorithmSpiRegistry;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 算法注册表（DDL 10.3 algorithm_registry；V2.2 产品化：专业算法注册/替换/匹配；
 * P3 插件化执行：注册算法可绑定 SPI 执行器（spi_key），决策编排真实计算并留痕）
 * 决策编排按智能体类目匹配最新 enabled 算法并执行对应 SPI；
 * 客户替换算法（新版本启用/停用旧版）即影响后续会话的决策过程计算与参数。
 */
@Service
public class AlgorithmService {

    private static final String[] CATEGORIES = {"forecast", "market_analysis", "quote_strategy",
            "risk_measure", "optimize", "settlement", "review", "rule_engine"};

    private final AlgorithmRegistryMapper algorithmRegistryMapper;
    private final AlgorithmSpiRegistry spiRegistry;

    public AlgorithmService(AlgorithmRegistryMapper algorithmRegistryMapper,
                            AlgorithmSpiRegistry spiRegistry) {
        this.algorithmRegistryMapper = algorithmRegistryMapper;
        this.spiRegistry = spiRegistry;
    }

    /** 懒种子（与 10_platform_config.sql 种子一致，表空时写入 9 条，幂等） */
    public void ensureAlgorithms() {
        Long count = algorithmRegistryMapper.selectCount(new LambdaQueryWrapper<AlgorithmRegistry>());
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"LSTM-PRICE-96", "LSTM 96 点价格预测", "forecast",
                        "负荷/新能源/价格历史特征序列预测，输出 96 点与置信带", "{\"horizon\":96,\"confidence_band\":90,\"lookback\":720}", "v1.2.0", "enabled"},
                {"SENTI-NEWS-1", "情报舆情情感分析", "market_analysis",
                        "近 24h 情报流关键词情感加权（[-1,1]），修正供需判断", "{\"window_hours\":24,\"high_weight\":1.5}", "v1.0.0", "enabled"},
                {"SEG-AGG-3PCT", "分段聚合报价（上浮 3%）", "quote_strategy",
                        "成本曲线分段聚合 + 基准情景报价，输出分段量价", "{\"segments\":8,\"uplift\":0.03}", "v1.1.0", "enabled"},
                {"MC-CVAR-95", "蒙特卡洛 CVaR(95%) 风险度量", "risk_measure",
                        "出清波动率情景压力测试，输出 CVaR/最大回撤/限价", "{\"scenarios\":10000,\"alpha\":0.95}", "v1.3.0", "enabled"},
                {"MILP-OPT-1", "混合整数规划联合优化", "optimize",
                        "申报/持仓/偏差考核约束下的收益最大化求解", "{\"solver\":\"cbc\",\"gap\":0.01}", "v1.0.0", "enabled"},
                {"DEV-ASSESS-1", "偏差考核结算测算", "settlement",
                        "结算收益与偏差考核风险预评估（规则阈值驱动）", "{\"dev_threshold\":0.05}", "v1.0.0", "enabled"},
                {"KB-REVIEW-1", "复盘知识库归纳", "review",
                        "决策-结果-原因-改进四段式复盘，结论回流策略库", "{\"template\":\"4step\"}", "v1.0.0", "enabled"},
                {"RULE-ENGINE-DROOLS", "规则引擎（合规校验）", "rule_engine",
                        "rule_config 活动版本规则实时校验，仲裁最高优先", "{\"engine\":\"drools\"}", "v2.1.0", "enabled"},
                {"HEDGE-STRATEGY-1", "省间价差套利策略", "optimize",
                        "省间通道价差监测与套利窗口识别（可选启用）", "{\"min_spread\":8,\"window\":4}", "v1.0.0", "disabled"},
        };
        long id = 92001;
        for (String[] s : seeds) {
            AlgorithmRegistry a = new AlgorithmRegistry();
            a.setId(id++);
            a.setAlgCode(s[0]);
            a.setAlgName(s[1]);
            a.setCategory(s[2]);
            a.setDescription(s[3]);
            a.setParamsSchema(s[4]);
            a.setVersion(s[5]);
            a.setStatus(s[6]);
            algorithmRegistryMapper.insert(a);
        }
    }

    /** 算法注册表（供算法管理页；按类目/状态筛选） */
    public List<Map<String, Object>> listAlgorithms(String category, String status) {
        ensureAlgorithms();
        LambdaQueryWrapper<AlgorithmRegistry> qw = new LambdaQueryWrapper<AlgorithmRegistry>()
                .eq(StrUtils.isNotBlank(category), AlgorithmRegistry::getCategory, category)
                .eq(StrUtils.isNotBlank(status), AlgorithmRegistry::getStatus, status)
                .orderByAsc(AlgorithmRegistry::getCategory)
                .orderByDesc(AlgorithmRegistry::getVersion);
        List<AlgorithmRegistry> list = algorithmRegistryMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (AlgorithmRegistry a : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(a.getId()));
            item.put("algCode", a.getAlgCode());
            item.put("algName", a.getAlgName());
            item.put("category", a.getCategory());
            item.put("description", a.getDescription());
            item.put("paramsSchema", a.getParamsSchema() == null ? "{}" : a.getParamsSchema());
            item.put("version", a.getVersion());
            item.put("spiKey", a.getSpiKey());
            item.put("status", a.getStatus());
            result.add(item);
        }
        return result;
    }

    /** SPI 执行器清单（算法注册/编辑下拉：绑定执行器，空=按类目默认） */
    public List<Map<String, Object>> listSpis() {
        return spiRegistry.listSpis();
    }

    /** 新增算法（编码+版本唯一；类目枚举校验；spiKey 须为已注册执行器或空=按类目默认） */
    public AlgorithmRegistry createAlgorithm(String algCode, String algName, String category,
                                             String description, String paramsSchema, String version,
                                             String spiKey, String status) {
        if (StrUtils.isBlank(algCode) || StrUtils.isBlank(algName) || StrUtils.isBlank(category)) {
            throw new ServiceException("算法编码/名称/类目不能为空");
        }
        if (!Arrays.asList(CATEGORIES).contains(category)) {
            throw new ServiceException("算法类目不合法：" + category);
        }
        if (StrUtils.isNotBlank(spiKey) && !spiRegistry.exists(spiKey)) {
            throw new ServiceException("SPI 执行器不存在：" + spiKey + "（可查询 /algorithm/spis）");
        }
        Long exists = algorithmRegistryMapper.selectCount(new LambdaQueryWrapper<AlgorithmRegistry>()
                .eq(AlgorithmRegistry::getAlgCode, algCode)
                .eq(AlgorithmRegistry::getVersion, StrUtils.isBlank(version) ? "v1.0.0" : version));
        if (exists != null && exists > 0) {
            throw new ServiceException("算法编码+版本已存在：" + algCode + " " + version);
        }
        AlgorithmRegistry a = new AlgorithmRegistry();
        a.setAlgCode(algCode);
        a.setAlgName(algName);
        a.setCategory(category);
        a.setDescription(description);
        a.setParamsSchema(StrUtils.isBlank(paramsSchema) ? "{}" : paramsSchema);
        a.setVersion(StrUtils.isBlank(version) ? "v1.0.0" : version);
        a.setSpiKey(spiKey);
        a.setStatus(StrUtils.isBlank(status) ? "enabled" : status);
        algorithmRegistryMapper.insert(a);
        return a;
    }

    /** 更新算法（参数模板/说明/版本/SPI 执行器/启停；替换算法 = 新版本启用 + 旧版停用） */
    public void updateAlgorithm(Long id, String algName, String description, String paramsSchema,
                                String version, String spiKey, String status) {
        AlgorithmRegistry exist = algorithmRegistryMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("算法不存在");
        }
        if (StrUtils.isNotBlank(spiKey) && !spiRegistry.exists(spiKey)) {
            throw new ServiceException("SPI 执行器不存在：" + spiKey + "（可查询 /algorithm/spis）");
        }
        AlgorithmRegistry update = new AlgorithmRegistry();
        update.setId(id);
        update.setAlgName(algName);
        update.setDescription(description);
        update.setParamsSchema(paramsSchema);
        update.setVersion(version);
        update.setSpiKey(spiKey);
        update.setStatus(status);
        algorithmRegistryMapper.updateById(update);
    }

    /**
     * 决策过程算法匹配：按类目取最新 enabled 算法（替换算法即调整该匹配结果），
     * 无匹配返回 null（编排保持原确定性逻辑，输出不标注算法）。
     */
    public Map<String, Object> matchAlgorithm(String category) {
        ensureAlgorithms();
        AlgorithmRegistry alg = algorithmRegistryMapper.selectOne(new LambdaQueryWrapper<AlgorithmRegistry>()
                .eq(AlgorithmRegistry::getCategory, category)
                .eq(AlgorithmRegistry::getStatus, "enabled")
                .orderByDesc(AlgorithmRegistry::getId)
                .last("LIMIT 1"));
        if (alg == null) {
            return null;
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("algCode", alg.getAlgCode());
        item.put("algName", alg.getAlgName());
        item.put("category", alg.getCategory());
        item.put("version", alg.getVersion());
        item.put("spiKey", alg.getSpiKey());
        item.put("paramsSchema", alg.getParamsSchema() == null ? "{}" : alg.getParamsSchema());
        return item;
    }

    /**
     * 算法文件自动解析（操作友好性：客户上传专业算法文件，系统自动解析注册要素并回填注册表单）。
     * 解析规则：文件名去扩展名→算法名称/编码；内容关键字猜类目（显式指定优先）；
     * .json 内容为合法 JSON 对象时采纳为参数模板；.py/.txt/.md 取正文前 300 字符为说明；
     * .jar/.zip 打包算法深度解析（V2.4；V2.5 算法包规范）：解压扫描 MANIFEST.MF 的 Main-Class/版本
     * 及 PTIDSS-Algorithm-* 规范属性、顶层 ptidss-algorithm.json 规范元数据（名称/类目/版本/说明/参数）、
     * 包内 params.json/config.json/algorithm.json 参数模板、README 说明、类名关键字猜类目；
     * 版本默认 v1.0.0。
     */
    public Map<String, Object> parseAlgorithmFile(String originalFilename, byte[] content,
                                                  long size, String category) {
        if (StrUtils.isBlank(originalFilename) || content == null || content.length == 0) {
            throw new ServiceException("算法文件不能为空");
        }
        String name = originalFilename;
        int dot = name.lastIndexOf('.');
        String ext = dot >= 0 ? name.substring(dot + 1).toLowerCase() : "";
        String base = dot >= 0 ? name.substring(0, dot) : name;
        // 算法编码：文件名去扩展名 → 非字母数字转 '-' → 大写；无可转字符时回退时间戳编码
        String codeBase = base.replaceAll("[^A-Za-z0-9]+", "-").replaceAll("^-|-$", "").toUpperCase();
        String algCode = StrUtils.isBlank(codeBase) ? "ALG-" + System.currentTimeMillis() : codeBase;
        if (algCode.length() > 50) {
            algCode = algCode.substring(0, 50);
        }
        // 类目：显式指定优先，其次按文件名/正文关键字猜测（默认 forecast）
        boolean textFile = "json".equals(ext) || "txt".equals(ext) || "md".equals(ext) || "py".equals(ext);
        boolean archive = "jar".equals(ext) || "zip".equals(ext);
        String bodyText = textFile ? new String(content, StandardCharsets.UTF_8) : "";
        // 打包算法：深度解析（MANIFEST/参数模板/README/文件清单）
        Map<String, Object> archiveInfo = archive ? parseArchive(content) : new LinkedHashMap<>();
        String archiveText = archive ? String.join("\n", (List<String>) archiveInfo.getOrDefault("files", new ArrayList<>())) : "";
        // 类目：显式指定 > 包规范元数据（ptidss-algorithm.json/MANIFEST 自定义属性）> 启发式猜测
        String metaCat = archive ? String.valueOf(archiveInfo.getOrDefault("category", "")) : "";
        String cat;
        if (StrUtils.isNotBlank(category)) {
            cat = category;
        } else if (StrUtils.isNotBlank(metaCat)) {
            cat = metaCat;
        } else {
            cat = guessCategory(name + "\n" + bodyText + "\n" + archiveText);
        }
        if (!Arrays.asList(CATEGORIES).contains(cat)) {
            if (StrUtils.isNotBlank(category)) {
                throw new ServiceException("算法类目不合法：" + cat);
            }
            // 规范元数据类目不合法时回落启发式猜测（不阻断上传）
            cat = guessCategory(name + "\n" + bodyText + "\n" + archiveText);
        }
        // 参数模板：.json 内容为合法 JSON 对象时直接采纳；打包算法取包内 params/config/algorithm.json
        String paramsSchema = "{}";
        if ("json".equals(ext)) {
            try {
                Object parsed = new ObjectMapper().readValue(bodyText, Object.class);
                if (parsed instanceof Map) {
                    paramsSchema = new ObjectMapper().writeValueAsString(parsed);
                }
            } catch (Exception ignored) {
                // 非 JSON 对象内容按普通文本处理
            }
        } else if (archive) {
            String inner = String.valueOf(archiveInfo.getOrDefault("paramsSchema", "{}"));
            if (!"{}".equals(inner) && StrUtils.isNotBlank(inner)) {
                paramsSchema = inner;
            }
        }
        // 说明：文本类取正文前 300 字符；打包算法取 README/清单说明；二进制包给出类型/大小说明
        String description = textFile ? firstLines(bodyText, 300) : "";
        if (archive) {
            String readme = String.valueOf(archiveInfo.getOrDefault("readme", ""));
            String manifestDesc = String.valueOf(archiveInfo.getOrDefault("manifestDesc", ""));
            String metaDesc = String.valueOf(archiveInfo.getOrDefault("description", ""));
            // 说明：规范元数据（ptidss-algorithm.json/MANIFEST）> README > 清单名称
            description = StrUtils.isNotBlank(metaDesc) ? metaDesc
                    : (StrUtils.isNotBlank(readme) ? readme : manifestDesc);
        }
        if (StrUtils.isBlank(description)) {
            String kind = "py".equals(ext) ? "Python 算法脚本"
                    : "jar".equals(ext) ? "Java 算法包"
                    : "zip".equals(ext) ? "算法打包文件" : "算法文件";
            description = "算法文件 " + originalFilename + "（" + size + " 字节，" + kind
                    + "），请补充算法说明与适用边界";
        }
        if (archive) {
            description = String.valueOf(archiveInfo.getOrDefault("summary", "")) + description;
        }
        String metaName = archive ? String.valueOf(archiveInfo.getOrDefault("name", "")) : "";
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("algCode", algCode);
        item.put("algName", StrUtils.isNotBlank(metaName) ? metaName : base);
        item.put("category", cat);
        item.put("description", description);
        item.put("paramsSchema", paramsSchema);
        item.put("version", StrUtils.isBlank(String.valueOf(archiveInfo.getOrDefault("version", "")))
                ? "v1.0.0" : String.valueOf(archiveInfo.get("version")));
        item.put("fileName", originalFilename);
        item.put("fileSize", size);
        item.put("extension", ext);
        item.put("archiveInfo", archiveInfo.isEmpty() ? null : archiveInfo);
        return item;
    }

    /**
     * 打包算法深度解析（V2.4；V2.5 算法包规范全自动适配）：
     * - META-INF/MANIFEST.MF：Main-Class / Implementation-Title / Implementation-Version，
     *   及规范自定义属性 PTIDSS-Algorithm-Name/Category/Version/Description；
     * - 包内顶层 ptidss-algorithm.json（规范元数据，优先级高于 MANIFEST 自定义属性）；
     * - 包内 params.json / config.json / algorithm.json / schema*.json → 参数模板（规范 params 优先）；
     * - README.md / README.txt → 算法说明（前 300 字符）；
     * - 文件清单 → 类目关键字猜测（类名含 Price/LSTM/Risk 等）。
     * 防护：单文件读取 ≤ 256KB、累计 ≤ 2MB（防 zip 炸弹）。
     */
    private Map<String, Object> parseArchive(byte[] content) {
        Map<String, Object> info = new LinkedHashMap<>();
        String paramsSchema = "{}";
        String readme = "";
        String manifestDesc = "";
        String mainClass = "";
        String version = "";
        List<String> files = new ArrayList<>();
        // V2.5 规范元数据：ptidss-algorithm.json 覆盖 MANIFEST 自定义属性（put 覆盖/putIfAbsent 兜底）
        Map<String, String> meta = new LinkedHashMap<>();
        Map<String, Object> metaParams = null;
        ObjectMapper mapper = new ObjectMapper();
        long totalRead = 0;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String entryName = entry.getName();
                files.add(entryName);
                String lower = entryName.toLowerCase();
                long remaining = 1024L * 1024 * 2 - totalRead;
                if (remaining <= 0) {
                    break;
                }
                if (lower.endsWith("manifest.mf") && lower.contains("meta-inf")) {
                    String text = readEntryLimited(zis, Math.min(64L * 1024, remaining));
                    totalRead += text.length();
                    for (String line : text.split("\\r?\\n")) {
                        String t = line.trim();
                        if (t.isEmpty()) {
                            continue;
                        }
                        String tl = t.toLowerCase();
                        int colon = t.indexOf(':');
                        if (colon <= 0) {
                            continue;
                        }
                        String value = t.substring(colon + 1).trim();
                        if (tl.startsWith("main-class:")) {
                            mainClass = value;
                        } else if (tl.startsWith("implementation-title:")) {
                            manifestDesc = value;
                        } else if (tl.startsWith("implementation-version:")) {
                            version = value;
                        } else if (tl.startsWith("ptidss-algorithm-name:")) {
                            meta.putIfAbsent("name", value);
                        } else if (tl.startsWith("ptidss-algorithm-category:")) {
                            meta.putIfAbsent("category", value);
                        } else if (tl.startsWith("ptidss-algorithm-version:")) {
                            meta.putIfAbsent("version", value);
                        } else if (tl.startsWith("ptidss-algorithm-description:")) {
                            meta.putIfAbsent("description", value);
                        }
                    }
                } else if (lower.equals("ptidss-algorithm.json") || lower.equals("./ptidss-algorithm.json")) {
                    // 算法包规范元数据（V2.5）：显式声明 name/category/version/description/params，覆盖 MANIFEST
                    String text = readEntryLimited(zis, Math.min(256L * 1024, remaining));
                    totalRead += text.length();
                    try {
                        Object parsed = mapper.readValue(text, Object.class);
                        if (parsed instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> j = (Map<String, Object>) parsed;
                            putIfNotBlank(meta, "name", j.get("name"));
                            putIfNotBlank(meta, "category", j.get("category"));
                            putIfNotBlank(meta, "version", j.get("version"));
                            putIfNotBlank(meta, "description", j.get("description"));
                            Object p = j.get("params");
                            if (p instanceof Map && !((Map<?, ?>) p).isEmpty()) {
                                metaParams = (Map<String, Object>) p;
                            } else if (p == null && j.get("paramsSchema") instanceof Map) {
                                metaParams = (Map<String, Object>) j.get("paramsSchema");
                            }
                        }
                    } catch (Exception ignored) {
                        // 规范元数据 JSON 非法时忽略，回落 MANIFEST/启发式解析
                    }
                } else if (lower.matches(".*(params|config|algorithm|schema)[^/]*\\.json$")) {
                    String text = readEntryLimited(zis, Math.min(256L * 1024, remaining));
                    totalRead += text.length();
                    try {
                        Object parsed = mapper.readValue(text, Object.class);
                        if (parsed instanceof Map) {
                            paramsSchema = mapper.writeValueAsString(parsed);
                        }
                    } catch (Exception ignored) {
                        // 非 JSON 对象内容跳过
                    }
                } else if (lower.matches(".*readme[^/]*(\\.md|\\.txt)?$")) {
                    String text = readEntryLimited(zis, Math.min(256L * 1024, remaining));
                    totalRead += text.length();
                    if (StrUtils.isBlank(readme)) {
                        readme = firstLines(text, 300);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("算法包解析失败（非标准 zip/jar 格式）：" + e.getMessage());
        }
        String metaName = meta.getOrDefault("name", "");
        String metaCategory = meta.getOrDefault("category", "");
        String metaDesc = meta.getOrDefault("description", "");
        String metaVersion = meta.getOrDefault("version", "");
        if (StrUtils.isNotBlank(metaVersion)) {
            version = metaVersion;  // 规范版本 > Implementation-Version
        }
        if (metaParams != null && !metaParams.isEmpty()) {
            try {
                paramsSchema = mapper.writeValueAsString(metaParams);  // 规范 params > 包内扫描模板
            } catch (Exception ignored) {
                // 保持包内扫描结果
            }
        }
        info.put("paramsSchema", paramsSchema);
        info.put("readme", readme);
        info.put("manifestDesc", manifestDesc);
        info.put("mainClass", mainClass);
        info.put("version", version);
        info.put("name", metaName);
        info.put("category", metaCategory);
        info.put("description", metaDesc);
        info.put("metaParams", metaParams);
        info.put("files", files);
        info.put("fileCount", files.size());
        StringBuilder summary = new StringBuilder();
        if (StrUtils.isNotBlank(mainClass)) {
            summary.append("入口类 ").append(mainClass).append("；");
        }
        if (StrUtils.isNotBlank(manifestDesc)) {
            summary.append("清单名称 ").append(manifestDesc).append("；");
        }
        if (StrUtils.isNotBlank(metaName)) {
            summary.append("规范名称 ").append(metaName).append("；");
        }
        if (StrUtils.isNotBlank(metaCategory)) {
            summary.append("规范类目 ").append(metaCategory).append("；");
        }
        if (StrUtils.isNotBlank(version)) {
            summary.append("清单版本 ").append(version).append("；");
        }
        if (!"{}".equals(paramsSchema)) {
            summary.append("包内参数模板已解析；");
        }
        if (files.size() > 0) {
            summary.append("包内 ").append(files.size()).append(" 个文件。");
        }
        info.put("summary", summary.toString());
        return info;
    }

    /** 规范元数据写入（仅非空白值，避免覆盖 MANIFEST 已有声明） */
    private void putIfNotBlank(Map<String, String> m, String key, Object value) {
        if (value != null && StrUtils.isNotBlank(String.valueOf(value))) {
            m.put(key, String.valueOf(value).trim());
        }
    }

    /** 受限读取 zip 条目文本（上限保护，防 zip 炸弹） */
    private String readEntryLimited(ZipInputStream zis, long maxBytes) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        long read = 0;
        try {
            int n;
            while ((n = zis.read(buf)) != -1 && read < maxBytes) {
                int take = (int) Math.min(n, maxBytes - read);
                out.write(buf, 0, take);
                read += take;
                if (read >= maxBytes) {
                    break;
                }
            }
        } catch (Exception e) {
            return "";
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /** 按文件名/正文关键字猜测算法类目（命中优先顺序：风控>报价>结算>复盘>优化>规则>行情>预测） */
    private String guessCategory(String text) {
        String t = text.toLowerCase();
        if (t.contains("risk") || t.contains("cvar") || t.contains("var")) {
            return "risk_measure";
        }
        if (t.contains("quote") || t.contains("offer") || t.contains("bid")) {
            return "quote_strategy";
        }
        if (t.contains("settle")) {
            return "settlement";
        }
        if (t.contains("review") || t.contains("复盘")) {
            return "review";
        }
        if (t.contains("optimize") || t.contains("milp") || t.contains("最优")) {
            return "optimize";
        }
        if (t.contains("rule") || t.contains("drools")) {
            return "rule_engine";
        }
        if (t.contains("market") || t.contains("senti") || t.contains("舆情")) {
            return "market_analysis";
        }
        return "forecast";
    }

    /** 正文压缩为单行并截断（说明回填用） */
    private String firstLines(String text, int max) {
        if (StrUtils.isBlank(text)) {
            return "";
        }
        String compact = text.replace('\r', ' ').replace('\n', ' ').replaceAll("\\s+", " ").trim();
        return compact.length() <= max ? compact : compact.substring(0, max) + "…";
    }
}

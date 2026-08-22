package com.ptidss.policy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.DateUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.policy.domain.PolicyAnalysis;
import com.ptidss.policy.domain.PolicyArticle;
import com.ptidss.policy.domain.PolicyDocument;
import com.ptidss.policy.domain.RuleConfig;
import com.ptidss.policy.mapper.PolicyAnalysisMapper;
import com.ptidss.policy.mapper.PolicyArticleMapper;
import com.ptidss.policy.mapper.PolicyDocumentMapper;
import com.ptidss.policy.mapper.RuleConfigMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 政策研判（对齐 OpenAPI V1.1 /policy/**；FR-PD-01 P0）
 * 业务规则：政策库分类/标签/版本管理；LLM 智能解析（抽取条款→规则候选，置信度标注）；
 * 影响研判（政策变化点→影响环节→影响程度，可追溯）；规则库沉淀（版本化，供合规校验复用）；
 * 研判简报一键导出；解析幂等（reparse 强制重解析）
 */
@Service
public class PolicyService {

    /** 种子政策文档（懒生成；覆盖国家/区域/省内分类与现货/中长期/结算/考核标签） */
    private static final String[][] SEED_DOCS = {
            {"电力现货市场基本规则（试行）", "国家能源局", "national", "现货,交易规则",
                    "2026-07-01", "2026-08-01"},
            {"关于深化电力市场化改革的意见", "国家发展改革委", "national", "中长期,结算",
                    "2026-06-15", "2026-07-15"},
            {"华东区域电力市场运营规则", "华东能源监管局", "regional", "现货,信息披露",
                    "2026-07-10", "2026-08-10"},
            {"省间现货交易实施细则", "长三角电力交易中心", "regional", "省间,交易规则",
                    "2026-05-20", "2026-06-20"},
            {"江苏电力现货市场结算规则", "江苏省发展改革委", "provincial", "结算,考核",
                    "2026-07-25", "2026-09-01"},
            {"中长期交易实施细则（2026 修订版）", "江苏电力交易中心", "provincial", "中长期,考核",
                    "2026-06-01", "2026-07-01"},
    };

    /** 条款类型与原文模板（LLM 解析确定性模拟） */
    private static final String[][] CLAUSE_TEMPLATES = {
            {"trade_rule", "交易申报采取分时报价方式，申报时段按小时划分，每段申报电量上限 {V} 兆瓦时",
                    "{\"segments\":24,\"max_volume\":{V}}", "compliance"},
            {"price_mechanism", "现货市场价格采用节点边际电价（LMP），出清价格上下限分别为 {floor} 与 {ceiling} 元/兆瓦时",
                    "{\"lmp\":true,\"floor\":0,\"ceiling\":{V}}", "compliance"},
            {"assessment", "偏差电量考核按 {V}% 浮动区间执行，超出部分按每兆瓦时考核",
                    "{\"band_pct\":{V},\"penalty\":50}", "assessment"},
            {"settlement", "结算周期按自然月执行，电费结算在次月 {V} 日前完成",
                    "{\"period\":\"monthly\",\"days\":{V}}", "decision"},
    };

    private final PolicyDocumentMapper policyDocumentMapper;
    private final PolicyArticleMapper policyArticleMapper;
    private final PolicyAnalysisMapper policyAnalysisMapper;
    private final RuleConfigMapper ruleConfigMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    /** 本地文件存储根目录（ptidss.storage.path；政策原文上传落盘） */
    @Value("${ptidss.storage.path:./data/ptidss}")
    private String storagePath;

    public PolicyService(PolicyDocumentMapper policyDocumentMapper,
                         PolicyArticleMapper policyArticleMapper,
                         PolicyAnalysisMapper policyAnalysisMapper,
                         RuleConfigMapper ruleConfigMapper,
                         SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.policyDocumentMapper = policyDocumentMapper;
        this.policyArticleMapper = policyArticleMapper;
        this.policyAnalysisMapper = policyAnalysisMapper;
        this.ruleConfigMapper = ruleConfigMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    // ---------- 政策库 ----------

    /** 政策文件表为空时写入种子（幂等，与 07_seed_data.sql 无 policy 种子段保持一致） */
    private void ensureDocuments() {
        Long count = policyDocumentMapper.selectCount(new LambdaQueryWrapper<PolicyDocument>());
        if (count != null && count > 0) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String[] d : SEED_DOCS) {
            PolicyDocument doc = new PolicyDocument();
            doc.setTitle(d[0]);
            doc.setIssuingBody(d[1]);
            doc.setCategory(d[2]);
            doc.setTags(toJson(java.util.Arrays.asList(d[3].split(","))));
            doc.setVersionNo(1);
            doc.setFileUrl("minio://policy/" + d[2] + "/" + System.currentTimeMillis() + ".pdf");
            try {
                doc.setPublishDate(sdf.parse(d[4]));
                doc.setEffectiveDate(sdf.parse(d[5]));
            } catch (Exception ignored) {
                doc.setPublishDate(new Date());
                doc.setEffectiveDate(new Date());
            }
            doc.setStatus("published");
            policyDocumentMapper.insert(doc);
        }
    }

    /** 上传/新建政策文档（登记入政策库；分类/状态枚举校验，fileUrl 缺省模拟 MinIO 对象地址） */
    @Transactional(rollbackFor = Exception.class)
    public PolicyDocument upload(String title, String issuingBody, String category, List<String> tags,
                                 String publishDate, String effectiveDate, String status, String fileUrl) {
        if (StrUtils.isBlank(title) || StrUtils.isBlank(category)) {
            throw new ServiceException("政策标题/分类不能为空");
        }
        List<String> categories = java.util.Arrays.asList("national", "regional", "provincial");
        if (!categories.contains(category)) {
            throw new ServiceException("政策分类不合法：" + category);
        }
        List<String> statuses = java.util.Arrays.asList("draft", "published", "expired");
        if (StrUtils.isNotBlank(status) && !statuses.contains(status)) {
            throw new ServiceException("政策状态不合法：" + status);
        }
        PolicyDocument doc = new PolicyDocument();
        doc.setTitle(title);
        doc.setIssuingBody(StrUtils.isBlank(issuingBody) ? "—" : issuingBody);
        doc.setCategory(category);
        doc.setTags(toJson(tags == null ? new java.util.ArrayList<>() : tags));
        doc.setVersionNo(1);
        doc.setFileUrl(StrUtils.isBlank(fileUrl)
                ? "minio://policy/upload/" + category + "/" + System.currentTimeMillis() + ".pdf" : fileUrl);
        Date publish = DateUtils.parseLenient(publishDate);
        Date effective = DateUtils.parseLenient(effectiveDate);
        if ((StrUtils.isNotBlank(publishDate) && publish == null)
                || (StrUtils.isNotBlank(effectiveDate) && effective == null)) {
            throw new ServiceException("日期格式不合法（支持 yyyy-MM-dd 或时间日期）");
        }
        doc.setPublishDate(publish == null ? new Date() : publish);
        doc.setEffectiveDate(effective == null ? new Date() : effective);
        doc.setStatus(StrUtils.isBlank(status) ? "published" : status);
        policyDocumentMapper.insert(doc);
        return doc;
    }

    /**
     * 上传新政策（multipart：政策原文文件落盘本地存储 + 登记入政策库，fileUrl=local://policy/{yyyyMMdd}/{文件}）。
     * 操作友好性：用户在政策中心直接选择政策文件即可完成上传，登记后可一键触发 LLM 解析。
     */
    public PolicyDocument uploadFile(String title, String issuingBody, String category, List<String> tags,
                                     String publishDate, String effectiveDate, String status,
                                     String originalFilename, byte[] content) {
        if (content == null || content.length == 0 || StrUtils.isBlank(originalFilename)) {
            throw new ServiceException("政策文件不能为空");
        }
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stored = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                + "_" + sanitizeFilename(originalFilename);
        File dir = new File(storagePath, "policy/" + dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ServiceException("政策存储目录创建失败：" + dir.getAbsolutePath());
        }
        File target = new File(dir, stored);
        try {
            Files.write(target.toPath(), content);
        } catch (IOException e) {
            throw new ServiceException("政策文件保存失败：" + e.getMessage());
        }
        try {
            return upload(title, issuingBody, category, tags, publishDate, effectiveDate, status,
                    "local://policy/" + dateDir + "/" + stored);
        } catch (ServiceException e) {
            // 登记校验失败时清理已落盘文件，避免孤儿文件
            try {
                Files.deleteIfExists(target.toPath());
            } catch (IOException ignored) {
                // 清理失败不影响主错误返回
            }
            throw e;
        }
    }

    /** 政策原文文件读取（含原始文件名；fileUrl 非 local:// 本地存储或文件缺失返回 null） */
    public Map<String, Object> readFile(Long id) {
        PolicyDocument doc = policyDocumentMapper.selectById(id);
        if (doc == null) {
            throw new ServiceException("政策文件不存在");
        }
        if (doc.getFileUrl() == null || !doc.getFileUrl().startsWith("local://")) {
            return null;
        }
        String rel = doc.getFileUrl().substring("local://".length());
        File f = new File(storagePath, rel);
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        try {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("data", Files.readAllBytes(f.toPath()));
            resp.put("name", f.getName());
            return resp;
        } catch (IOException e) {
            throw new ServiceException("政策文件读取失败：" + e.getMessage());
        }
    }

    /** 文件名清洗（去路径分隔符/控制字符，防路径穿越） */
    private String sanitizeFilename(String name) {
        String cleaned = name.replaceAll("[/\\\\]", "_").replaceAll("[\\p{Cntrl}]", "");
        return StrUtils.isBlank(cleaned) ? "policy.pdf" : cleaned;
    }

    /** 政策文件分页列表（分类/关键词/状态多维筛选） */
    public Result<Page<PolicyDocument>> list(String category, String keyword,
                                             String status, long pageNo, long pageSize) {
        ensureDocuments();
        LambdaQueryWrapper<PolicyDocument> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(category), PolicyDocument::getCategory, category)
                .like(StrUtils.isNotBlank(keyword), PolicyDocument::getTitle, keyword)
                .eq(StrUtils.isNotBlank(status), PolicyDocument::getStatus, status)
                .orderByDesc(PolicyDocument::getPublishDate);
        return Result.success(policyDocumentMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    /** 政策详情（含解析条款/影响研判/沉淀规则；PolicyDetail 契约） */
    public Map<String, Object> detail(Long id) {
        ensureDocuments();
        PolicyDocument doc = policyDocumentMapper.selectById(id);
        if (doc == null) {
            throw new ServiceException("政策文件不存在");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", String.valueOf(doc.getId()));
        resp.put("title", doc.getTitle());
        resp.put("issuingBody", doc.getIssuingBody());
        resp.put("category", doc.getCategory());
        resp.put("tags", doc.getTags());
        resp.put("fileUrl", doc.getFileUrl());
        resp.put("effectiveDate", doc.getEffectiveDate());
        resp.put("publishDate", doc.getPublishDate());
        resp.put("versionNo", doc.getVersionNo());
        resp.put("status", doc.getStatus());
        resp.put("articles", listArticles(id));
        resp.put("analysis", listAnalysis(id));
        resp.put("rules", listRules(id));
        return resp;
    }

    // ---------- 智能解析（LLM 抽取条款→结构化规则候选） ----------

    /** 解析政策文档（确定性模拟；幂等：已解析且未强制重解析时返回既有统计；多表写入原子提交） */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> parse(Long policyId, boolean reparse) {
        ensureDocuments();
        PolicyDocument doc = policyDocumentMapper.selectById(policyId);
        if (doc == null) {
            throw new ServiceException("政策文件不存在");
        }
        long articleCount = countArticles(policyId);
        if (articleCount > 0 && !reparse) {
            return parseStats(policyId);
        }
        if (reparse) {
            // 强制重解析：旧条款/研判作废重建，规则沉淀追加新版本（保留留痕）
            policyArticleMapper.delete(new LambdaQueryWrapper<PolicyArticle>()
                    .eq(PolicyArticle::getPolicyId, policyId));
            policyAnalysisMapper.delete(new LambdaQueryWrapper<PolicyAnalysis>()
                    .eq(PolicyAnalysis::getPolicyId, policyId));
        }
        // 确定性模拟：policyId 截断为 0-99 种子（避免雪花 ID 截断溢出为负数）
        int seed = (int) (policyId % 100);
        int articleNum = 3 + (seed % 3);           // 3-5 条条款
        List<PolicyArticle> articles = new ArrayList<>();
        List<PolicyAnalysis> analyses = new ArrayList<>();
        List<RuleConfig> rules = new ArrayList<>();
        // 本事务内已分配版本跟踪（同类型条款重复时避免一级缓存读到旧版本导致唯一索引冲突）
        Map<String, Integer> localVersions = new java.util.HashMap<>();
        for (int i = 0; i < articleNum; i++) {
            String[] tpl = CLAUSE_TEMPLATES[(seed + i) % CLAUSE_TEMPLATES.length];
            int value = 10 + (seed * 7 + i * 13) % 90;   // 确定性参数
            String original = tpl[1].replace("{V}", String.valueOf(value))
                    .replace("{floor}", "0").replace("{ceiling}", "1500");
            BigDecimal confidence = new BigDecimal("0.85")
                    .add(new BigDecimal((seed * 3 + i) % 13).movePointLeft(2));  // 0.85-0.97
            PolicyArticle article = new PolicyArticle();
            article.setPolicyId(policyId);
            article.setClauseType(tpl[0]);
            article.setOriginalText(original);
            article.setParsedStructure(tpl[2].replace("{V}", String.valueOf(value))
                    .replace("{floor}", "0").replace("{ceiling}", "1500"));
            article.setConfidence(confidence);
            article.setReviewStatus("pending");
            policyArticleMapper.insert(article);
            articles.add(article);
            // 影响研判：变化点→影响环节→影响程度（可追溯）
            PolicyAnalysis analysis = new PolicyAnalysis();
            analysis.setPolicyId(policyId);
            analysis.setChangePoint(tpl[0].equals("trade_rule") ? "申报时段划分调整"
                    : tpl[0].equals("price_mechanism") ? "出清价格上下限调整"
                    : tpl[0].equals("assessment") ? "偏差考核区间调整" : "结算周期调整");
            String[] links = {"预测", "决策", "申报", "结算", "考核"};
            analysis.setAffectedLink(links[(seed + i) % links.length]);
            String[] levels = {"low", "medium", "high"};
            analysis.setImpactLevel(levels[(seed + i) % levels.length]);
            analysis.setAnalysisResult(toJson(java.util.Collections.singletonMap("conclusion",
                    "对" + analysis.getAffectedLink() + "环节产生" + analysis.getImpactLevel() + "影响，"
                            + "建议结合历史交易数据评估策略调整，分析记录可追溯")));
            analysis.setAnalyst(securityUtils.getUsername());
            policyAnalysisMapper.insert(analysis);
            analyses.add(analysis);
            // 规则库沉淀（版本化；同编码最高版本 +1，本地跟踪防一级缓存读旧值）
            // 注意：buildRule 内部已执行 insert（insert 后雪花 ID 回填实体），此处不可重复插入
            RuleConfig rule = buildRule(policyId, article, tpl[3], value, localVersions);
            rules.add(rule);
            article.setRelatedRuleId(rule.getId());
            policyArticleMapper.updateById(article);
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", "PTASK-" + System.currentTimeMillis());
        resp.put("articlesParsed", articles.size());
        resp.put("ruleCandidates", rules.size());
        resp.put("avgConfidence", articles.stream()
                .map(PolicyArticle::getConfidence)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(articles.size()), 2, RoundingMode.HALF_UP));
        resp.put("analyses", analyses.size());
        return resp;
    }

    /** 规则沉淀：编码 RULE-POLICY-{类型}，版本 = max(历史最高, 本事务已分配) + 1（与 07 种子 RULE-* 前缀区分） */
    private RuleConfig buildRule(Long policyId, PolicyArticle article, String ruleType, int value,
                                 Map<String, Integer> localVersions) {
        String code = "RULE-POLICY-" + article.getClauseType().toUpperCase()
                .replace("_", "-");
        Integer maxVersion = ruleConfigMapper.selectList(
                        new LambdaQueryWrapper<RuleConfig>()
                                .eq(RuleConfig::getRuleCode, code)
                                .select(RuleConfig::getVersion))
                .stream().map(RuleConfig::getVersion).max(Integer::compareTo).orElse(0);
        int version = Math.max(maxVersion, localVersions.getOrDefault(code, 0)) + 1;
        localVersions.put(code, version);
        RuleConfig rule = new RuleConfig();
        rule.setRuleCode(code);
        rule.setRuleName(policyDocumentMapper.selectById(policyId).getTitle()
                + " · " + article.getClauseType() + " 条款");
        rule.setRuleType(ruleType);
        rule.setParams(article.getParsedStructure());
        rule.setVersion(version);
        Calendar cal = Calendar.getInstance();
        rule.setEffectiveDate(cal.getTime());
        cal.add(Calendar.YEAR, 5);
        rule.setExpiredDate(cal.getTime());
        rule.setSourcePolicyId(policyId);
        rule.setStatus("draft");
        ruleConfigMapper.insert(rule);
        return rule;
    }

    /** 已解析统计（幂等返回） */
    private Map<String, Object> parseStats(Long policyId) {
        List<Map<String, Object>> articles = listArticles(policyId);
        long rules = listRules(policyId).size();
        BigDecimal avg = articles.isEmpty() ? BigDecimal.ZERO
                : articles.stream()
                        .map(a -> new BigDecimal(String.valueOf(a.get("confidence"))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(articles.size()), 2, RoundingMode.HALF_UP);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", "PTASK-" + System.currentTimeMillis());
        resp.put("articlesParsed", articles.size());
        resp.put("ruleCandidates", rules);
        resp.put("avgConfidence", avg);
        return resp;
    }

    // ---------- 研判简报 ----------

    /** 政策研判简报导出（报送格式：政策信息 + 条款表 + 研判表 + 规则表，UTF-8 CSV） */
    public byte[] brief(Long id) {
        Map<String, Object> detail = detail(id);
        StringBuilder sb = new StringBuilder();
        sb.append("政策研判简报\n");
        sb.append("标题,").append(detail.get("title")).append('\n');
        sb.append("发布机构,").append(detail.get("issuingBody")).append('\n');
        sb.append("分类,").append(detail.get("category")).append('\n');
        sb.append("生效日期,").append(detail.get("effectiveDate")).append('\n');
        sb.append("生成时间,").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .append('\n');
        sb.append("\n一、解析条款（置信度/人工确认状态）\n");
        sb.append("条款类型,原文,置信度,复核状态\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> articles = (List<Map<String, Object>>) detail.get("articles");
        for (Map<String, Object> a : articles) {
            sb.append(a.get("clauseType")).append(',')
                    .append(String.valueOf(a.get("originalText")).replace('\n', ' ')).append(',')
                    .append(a.get("confidence")).append(',')
                    .append(a.get("reviewStatus")).append('\n');
        }
        sb.append("\n二、影响研判（变化点→影响环节→影响程度）\n");
        sb.append("变化点,影响环节,影响程度,研判人\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> analyses = (List<Map<String, Object>>) detail.get("analysis");
        for (Map<String, Object> an : analyses) {
            sb.append(an.get("changePoint")).append(',')
                    .append(an.get("affectedLink")).append(',')
                    .append(an.get("impactLevel")).append(',')
                    .append(an.get("analyst")).append('\n');
        }
        sb.append("\n三、规则库沉淀（版本化，供合规校验复用）\n");
        sb.append("规则编码,规则名称,类型,版本,状态\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rules = (List<Map<String, Object>>) detail.get("rules");
        for (Map<String, Object> r : rules) {
            sb.append(r.get("ruleCode")).append(',')
                    .append(r.get("ruleName")).append(',')
                    .append(r.get("ruleType")).append(',')
                    .append(r.get("version")).append(',')
                    .append(r.get("status")).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ---------- 内部查询 ----------

    private long countArticles(Long policyId) {
        return policyArticleMapper.selectCount(new LambdaQueryWrapper<PolicyArticle>()
                .eq(PolicyArticle::getPolicyId, policyId));
    }

    private List<Map<String, Object>> listArticles(Long policyId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PolicyArticle a : policyArticleMapper.selectList(new LambdaQueryWrapper<PolicyArticle>()
                .eq(PolicyArticle::getPolicyId, policyId)
                .orderByAsc(PolicyArticle::getClauseType))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", String.valueOf(a.getId()));
            m.put("clauseType", a.getClauseType());
            m.put("originalText", a.getOriginalText());
            m.put("parsedStructure", a.getParsedStructure());
            m.put("confidence", a.getConfidence());
            m.put("relatedRuleId", a.getRelatedRuleId());
            m.put("reviewStatus", a.getReviewStatus());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> listAnalysis(Long policyId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PolicyAnalysis an : policyAnalysisMapper.selectList(new LambdaQueryWrapper<PolicyAnalysis>()
                .eq(PolicyAnalysis::getPolicyId, policyId)
                .orderByDesc(PolicyAnalysis::getImpactLevel))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", String.valueOf(an.getId()));
            m.put("changePoint", an.getChangePoint());
            m.put("affectedLink", an.getAffectedLink());
            m.put("impactLevel", an.getImpactLevel());
            m.put("analysisResult", an.getAnalysisResult());
            m.put("analyst", an.getAnalyst());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> listRules(Long policyId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RuleConfig r : ruleConfigMapper.selectList(new LambdaQueryWrapper<RuleConfig>()
                .eq(RuleConfig::getSourcePolicyId, policyId)
                .orderByAsc(RuleConfig::getRuleCode)
                .orderByDesc(RuleConfig::getVersion))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", String.valueOf(r.getId()));
            m.put("ruleCode", r.getRuleCode());
            m.put("ruleName", r.getRuleName());
            m.put("ruleType", r.getRuleType());
            m.put("params", r.getParams());
            m.put("version", r.getVersion());
            m.put("status", r.getStatus());
            list.add(m);
        }
        return list;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败");
        }
    }
}

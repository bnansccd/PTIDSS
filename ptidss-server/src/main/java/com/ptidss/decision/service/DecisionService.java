package com.ptidss.decision.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.agent.domain.AgentRegistry;
import com.ptidss.agent.domain.AgentRun;
import com.ptidss.agent.mapper.AgentRegistryMapper;
import com.ptidss.agent.mapper.AgentRunMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.IdUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.common.utils.SnowflakeIdGenerator;
import com.ptidss.decision.domain.DecisionSession;
import com.ptidss.decision.dto.DecisionModifyRequest;
import com.ptidss.decision.dto.DecisionSessionRequest;
import com.ptidss.decision.dto.DecisionSessionView;
import com.ptidss.decision.mapper.DecisionSessionMapper;
import com.ptidss.intel.domain.IntelNews;
import com.ptidss.intel.mapper.IntelNewsMapper;
import com.ptidss.model.service.AlgorithmService;
import com.ptidss.model.service.LlmModelService;
import com.ptidss.model.service.ModelService;
import com.ptidss.policy.domain.RuleConfig;
import com.ptidss.policy.mapper.RuleConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 辅助决策（DDL 7.3 decision_session；多智能体协同编排 + 人机协同状态机）
 * 对齐 OpenAPI V1.0 /decision/**：依据链可回溯（FR-TR-05）、修改必须记录依据（FR-DM-05）
 * 编排（SRS FR-DM-01）：市场情报 → 电价预测 → 持仓合规 → 报价策略 → 风险评估 → 结算测算 → 复盘建议 → 综合决策
 * 七大智能体（DB 枚举）：forecast/market/quote/risk/compliance/settlement/review，
 *   运行记录持久化 agent_run（SRS FR-DM-02：输入快照/输出/置信度/耗时），
 *   仲裁优先级 合规 > 风险 > 收益（SRS R1），置信度 <0.7 强制人工关注（SRS R3），
 *   超时降级：跳过非关键智能体并标记（SRS R1）。
 * 说明：智能体当前为确定性模拟（种子=交易日期），模型平台接入后替换 Orchestrator 实现
 */
@Slf4j
@Service
public class DecisionService {

    /** 编排序列（SRS FR-DM-01 顺序；综合决策为编排器最后步骤，不落 agent_run） */
    private static final List<String> DEFAULT_AGENTS = Arrays.asList(
            "market", "forecast", "compliance", "quote", "risk", "settlement", "review");
    private static final String ORCHESTRATOR_VERSION = "orchestrator-v0.2-mock";
    /** 补跑版本（rerun 会话编排版本号，区分首次编排） */
    private static final String RERUN_VERSION = "orchestrator-v0.2-rerun";
    /** 冲突仲裁阈值：报价智能体与风险智能体价差超过该值（元/MWh）记冲突 */
    private static final double CONFLICT_PRICE_DELTA = 60.0;
    /** 修改量超过该比例（%）需双人复核 */
    private static final double REVIEW2_THRESHOLD = 15.0;
    /** 置信度低于该值强制人工关注（SRS FR-DM-02 R3） */
    private static final double CONFIDENCE_LOW = 0.7;
    /** 编排超时预算：日滚动 ≤5min、报价方案 ≤3min（SRS FR-DM-01 R1，毫秒） */
    private static final long TIMEOUT_ROLLING = 300_000L;
    private static final long TIMEOUT_QUOTE = 180_000L;
    /** 情报评分窗口：近 24 小时（情报→决策联动，FR-INT-04 接入决策链路） */
    private static final long INTEL_WINDOW_MS = 24 * 3600_000L;

    private final DecisionSessionMapper decisionSessionMapper;
    private final AgentRunMapper agentRunMapper;
    private final AgentRegistryMapper agentRegistryMapper;
    private final RuleConfigMapper ruleConfigMapper;
    private final IntelNewsMapper intelNewsMapper;
    private final ModelService modelService;
    private final AlgorithmService algorithmService;
    private final LlmModelService llmModelService;
    private final com.ptidss.model.spi.AlgorithmSpiRegistry algorithmSpiRegistry;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public DecisionService(DecisionSessionMapper decisionSessionMapper, AgentRunMapper agentRunMapper,
                           AgentRegistryMapper agentRegistryMapper, RuleConfigMapper ruleConfigMapper,
                           IntelNewsMapper intelNewsMapper, ModelService modelService,
                           AlgorithmService algorithmService, LlmModelService llmModelService,
                           com.ptidss.model.spi.AlgorithmSpiRegistry algorithmSpiRegistry,
                           SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.decisionSessionMapper = decisionSessionMapper;
        this.agentRunMapper = agentRunMapper;
        this.agentRegistryMapper = agentRegistryMapper;
        this.ruleConfigMapper = ruleConfigMapper;
        this.intelNewsMapper = intelNewsMapper;
        this.modelService = modelService;
        this.algorithmService = algorithmService;
        this.llmModelService = llmModelService;
        this.algorithmSpiRegistry = algorithmSpiRegistry;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    /** 发起决策会话：智能体编排（模拟执行）→ 冲突仲裁 → 综合决策 → agent_run 持久化 */
    public DecisionSessionView createSession(DecisionSessionRequest req) {
        if (StrUtils.isBlank(req.getSessionType()) || req.getTradeDate() == null) {
            throw new ServiceException("会话类型/交易日期不能为空");
        }
        List<String> agents = req.getAgents() == null || req.getAgents().isEmpty()
                ? DEFAULT_AGENTS : normalizeAgents(req.getAgents());
        String scenario = StrUtils.isBlank(req.getScenario()) ? "baseline" : req.getScenario();
        String sessionNo = "DS" + new SimpleDateFormat("yyyyMMdd").format(req.getTradeDate())
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Map<String, Object> result = orchestrate(req.getSessionType(), scenario, req.getTradeDate(),
                agents, sessionNo, true, "");
        List<Map<String, Object>> runs = castList(result.get("runs"));
        List<Map<String, Object>> conflicts = castList(result.get("conflicts"));
        Map<String, Object> strategy = castMap(result.get("strategy"));

        // 落库
        DecisionSession s = new DecisionSession();
        s.setId(SnowflakeIdGenerator.nextId());
        s.setSessionNo(sessionNo);
        s.setSessionType(req.getSessionType());
        s.setTradeDate(req.getTradeDate());
        s.setOrchestratorVersion(ORCHESTRATOR_VERSION);
        s.setAgents(toJson(agents));
        s.setFinalStrategy(toJson(strategy));
        s.setEvidenceChain(toJson(runs));
        s.setConflictRecords(toJson(conflicts));
        s.setHumanReviewStatus("pending");
        decisionSessionMapper.insert(s);
        return toView(s, runs, strategy);
    }

    /**
     * 情报触发式重算（FR-INT-04 深化）：对待审（pending）会话按最新情报流重评情报评分，
     * 更新 final_strategy 的情报快照（intel）并留痕"情报变更重评"。不重跑智能体编排，
     * 仅刷新情报影响标注，供人工在确认前感知情报变化（P3 增强：情报推送自动触发批量重评）。
     */
    public DecisionSessionView reassessIntel(Long sessionId) {
        DecisionSession s = getEntity(sessionId);
        if (!"pending".equals(s.getHumanReviewStatus())) {
            throw new ServiceException("仅待审（pending）会话可情报重评，当前状态：" + s.getHumanReviewStatus());
        }
        Map<String, Object> strategy = parseJson(s.getFinalStrategy());
        Map<String, Object> brief = intelImpact();
        strategy.put("intel", brief);
        String note = String.valueOf(strategy.getOrDefault("note", ""));
        strategy.put("note", note + "；情报变更重评（"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + "）：评分 " + String.format("%.2f", ((Number) brief.get("score")).doubleValue())
                + "（" + brief.get("window") + "，共 " + brief.get("count") + " 条）");
        DecisionSession update = new DecisionSession();
        update.setId(sessionId);
        update.setFinalStrategy(toJson(strategy));
        update.setReviewedBy(securityUtils.getUsername());
        update.setReviewedAt(new Date());
        decisionSessionMapper.updateById(update);
        return getSession(sessionId);
    }

    /** 批量情报重评（情报推送触发式重算）：近 24h 创建的待审会话逐个重评，返回重评数量 */
    public int reassessPendingSessions() {
        Date since = new Date(System.currentTimeMillis() - INTEL_WINDOW_MS);
        List<DecisionSession> pending = decisionSessionMapper.selectList(new LambdaQueryWrapper<DecisionSession>()
                .eq(DecisionSession::getHumanReviewStatus, "pending")
                .ge(DecisionSession::getCreatedAt, since));
        int count = 0;
        for (DecisionSession s : pending) {
            try {
                reassessIntel(s.getId());
                count++;
            } catch (Exception e) {
                log.warn("会话 {} 情报重评失败：{}", s.getSessionNo(), e.getMessage());
            }
        }
        return count;
    }

    /** 降级补跑（SRS FR-DM-01 R1）：对 degraded 会话重新完整编排（不模拟超时），
     * 补齐缺失智能体并重写 strategy/evidence_chain/conflict_records，agent_run 追加 -R 记录。
     * 仅 pending 状态可补跑；已确认/修改/驳回会话不可补跑。
     */
    public DecisionSessionView rerunSession(Long sessionId) {
        DecisionSession s = getEntity(sessionId);
        if (!"pending".equals(s.getHumanReviewStatus())) {
            throw new ServiceException("仅待审（pending）会话可补跑，当前状态：" + s.getHumanReviewStatus());
        }
        Map<String, Object> oldStrategy = parseJson(s.getFinalStrategy());
        if (!Boolean.TRUE.equals(oldStrategy.get("degraded"))) {
            throw new ServiceException("会话无降级智能体，无需补跑");
        }
        List<String> agents = parseStringList(s.getAgents());
        String scenario = String.valueOf(oldStrategy.getOrDefault("scenario", "baseline"));
        Map<String, Object> result = orchestrate(s.getSessionType(), scenario, s.getTradeDate(),
                agents, s.getSessionNo(), false, "-R");
        List<Map<String, Object>> runs = castList(result.get("runs"));
        List<Map<String, Object>> conflicts = castList(result.get("conflicts"));
        Map<String, Object> strategy = castMap(result.get("strategy"));
        strategy.put("note", String.valueOf(strategy.getOrDefault("note", ""))
                + "；已人工补跑：" + securityUtils.getUsername() + "（" + RERUN_VERSION + "），缺失智能体已补齐");

        DecisionSession update = new DecisionSession();
        update.setId(sessionId);
        update.setOrchestratorVersion(RERUN_VERSION);
        update.setFinalStrategy(toJson(strategy));
        update.setEvidenceChain(toJson(runs));
        update.setConflictRecords(toJson(conflicts));
        update.setReviewedBy(securityUtils.getUsername());
        update.setReviewedAt(new Date());
        decisionSessionMapper.updateById(update);
        return getSession(sessionId);
    }

    /** 编排主体：智能体执行（含情报评分/模型接入/降级模拟）→ 仲裁 → 综合决策；createSession/rerunSession 共用 */
    private Map<String, Object> orchestrate(String sessionType, String scenario, Date tradeDate,
                                            List<String> agents, String sessionNo,
                                            boolean allowDegrade, String runSuffix) {
        long seed = tradeDate.getTime();
        Map<String, Object> brief = intelImpact();
        double sentiment = ((Number) brief.get("score")).doubleValue();
        long timeoutBudget = "spot_quote".equals(sessionType) ? TIMEOUT_QUOTE : TIMEOUT_ROLLING;
        List<Map<String, Object>> runs = new ArrayList<>();
        Map<String, Object> quoteOut = null;
        Map<String, Object> riskOut = null;
        Map<String, Object> complianceOut = null;
        for (String code : agents) {
            Map<String, Object> run = runAgent(code, seed, scenario, sessionType, sentiment, brief);
            // 超时降级：超过预算跳过非关键智能体并标记（SRS FR-DM-01 R1；模拟场景）
            if (allowDegrade && ("review".equals(code) || "settlement".equals(code))
                    && "spot_quote".equals(sessionType) && "aggressive".equals(scenario)) {
                run.put("status", "timeout");
                run.put("degraded", true);
                run.put("output", "编排超时预算 " + timeoutBudget + "ms，跳过非关键智能体（降级标记，可人工补跑）");
            }
            recordAgentRun(sessionNo, runs.size(), run, runSuffix);
            runs.add(run);
            if ("quote".equals(code)) {
                quoteOut = run;
            }
            if ("risk".equals(code)) {
                riskOut = run;
            }
            if ("compliance".equals(code)) {
                complianceOut = run;
            }
        }
        // 冲突仲裁（SRS FR-DM-02 R1 优先级：合规 > 风险 > 收益）
        List<Map<String, Object>> conflicts = arbitrate(quoteOut, riskOut, complianceOut);
        // 综合决策（编排器最后步骤：加权聚合 + 低置信度标注 + 最终策略）
        Map<String, Object> strategy = buildStrategy(sessionType, scenario, seed, quoteOut, riskOut, runs);
        // 情报快照落策略（情报→决策联动：评分/窗口/影响情报随会话留痕，支持情报触发式重算）
        strategy.put("intel", brief);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("runs", runs);
        result.put("conflicts", conflicts);
        result.put("strategy", strategy);
        return result;
    }

    /** 旧编码兼容（predict → forecast），并过滤未注册编码 */
    private List<String> normalizeAgents(List<String> agents) {
        List<String> result = new ArrayList<>();
        for (String code : agents) {
            if ("predict".equals(code)) {
                code = "forecast";
            }
            if (DEFAULT_AGENTS.contains(code)) {
                result.add(code);
            }
        }
        return result.isEmpty() ? DEFAULT_AGENTS : result;
    }

    /** 冲突仲裁：合规校验优先（不通过则强制调整报价）、报价 vs 风险价差仲裁（SRS R1） */
    private List<Map<String, Object>> arbitrate(Map<String, Object> quoteOut, Map<String, Object> riskOut,
                                                Map<String, Object> complianceOut) {
        List<Map<String, Object>> conflicts = new ArrayList<>();
        if (complianceOut != null && Boolean.FALSE.equals(complianceOut.get("passed"))) {
            Map<String, Object> conflict = new LinkedHashMap<>();
            conflict.put("between", new String[]{"compliance", "quote"});
            conflict.put("reason", "合规智能体校验未通过：" + complianceOut.get("violations"));
            conflict.put("arbitration", "仲裁优先级 合规 > 风险 > 收益：按合规上限强制调整报价方案");
            conflict.put("resolvedPrice", complianceOut.get("limitPrice"));
            conflicts.add(conflict);
        }
        if (quoteOut != null && riskOut != null) {
            double quotePrice = ((Number) quoteOut.get("outputPrice")).doubleValue();
            double riskPrice = ((Number) riskOut.get("outputPrice")).doubleValue();
            if (Math.abs(quotePrice - riskPrice) > CONFLICT_PRICE_DELTA) {
                Map<String, Object> conflict = new LinkedHashMap<>();
                conflict.put("between", new String[]{"quote", "risk"});
                conflict.put("reason", "报价智能体与风险智能体建议价差 "
                        + Math.abs(quotePrice - riskPrice) + " 元/MWh 超过阈值 " + CONFLICT_PRICE_DELTA);
                conflict.put("arbitration", "仲裁优先级 合规 > 风险 > 收益：风险中性偏好下取两建议价均值");
                conflict.put("resolvedPrice", Math.round((quotePrice + riskPrice) / 2 * 100.0) / 100.0);
                conflicts.add(conflict);
            }
        }
        return conflicts;
    }

    /** 智能体执行：七大智能体输出不同维度结论（情报/预测/合规/报价/风险/结算/复盘） */
    private Map<String, Object> runAgent(String code, long seed, String scenario, String sessionType,
                                         double sentiment, Map<String, Object> brief) {
        double bias = scenarioFactor(scenario);
        double base = 380 + ((seed / 3600000) % 200) + bias;   // 日前均价基础值
        Map<String, Object> run = new LinkedHashMap<>();
        run.put("agentCode", code);
        run.put("reasoning", new LinkedHashMap<>());
        switch (code) {
            case "market":
                run.put("inputSnapshot", "现货出清价/供需平衡/省间通道容量 + 近24h情报流（" + brief.get("count") + " 条）");
                run.put("output", "市场情报研判：供需比 "
                        + Math.round((0.95 + (seed % 20) / 100.0) * 100.0) / 100.0
                        + "，价格趋势 " + (seed % 2 == 0 ? "上行" : "震荡")
                        + "，省间价差 " + Math.round(base * 0.08 * 100.0) / 100.0 + " 元/MWh；"
                        + "情报评分 " + String.format("%.2f", sentiment)
                        + "（" + brief.get("impact") + "）");
                run.put("outputPrice", Math.round(base * (1 + sentiment * 0.05) * 100.0) / 100.0);
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "基于近 7 日供需平衡数据与省间通道占用率（FR-PD-02 市场分析），"
                                + "并接入近 24h 情报流舆情/供需评分（FR-INT-04 情报→决策联动）");
                break;
            case "forecast":
                run.put("inputSnapshot", "负荷/新能源出力/历史价格 96 点特征（含情报修正 " + String.format("%.2f", sentiment) + "）");
                run.put("output", "次日均价预测 " + Math.round(base * (1 + sentiment * 0.03) * 100.0) / 100.0
                        + " 元/MWh，峰谷差 "
                        + Math.round((base * 0.45) * 100.0) / 100.0 + " 元/MWh（90% 置信区间）");
                run.put("outputPrice", Math.round(base * (1 + sentiment * 0.03) * 100.0) / 100.0);
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "price_v1 模型推理，输入特征版本 feat-最新（FR-PD-03 预测大模型），舆情修正系数 "
                                + String.format("%.2f", sentiment * 0.03));
                break;
            case "compliance":
                run.put("inputSnapshot", "申报规则库（rule_config active 版本）");
                List<RuleConfig> rules = ruleConfigMapper.selectList(new LambdaQueryWrapper<RuleConfig>()
                        .eq(RuleConfig::getStatus, "active")
                        .orderByAsc(RuleConfig::getRuleCode));
                StringBuilder ruleDesc = new StringBuilder();
                int segLimit = 10;
                for (RuleConfig r : rules) {
                    ruleDesc.append(r.getRuleCode()).append(" v").append(r.getVersion()).append("、");
                    if ("RULE-DECL-SEG".equals(r.getRuleCode())) {
                        segLimit = Math.max(4, segLimit);
                    }
                }
                String ruleRef = ruleDesc.length() == 0 ? "无 active 规则" : ruleDesc.substring(0, ruleDesc.length() - 1);
                boolean passed = segLimit >= 8;
                run.put("passed", passed);
                run.put("violations", passed ? new ArrayList<String>() : Arrays.asList("申报段数超出规则上限"));
                run.put("limitPrice", Math.round(base * 1.05 * 100.0) / 100.0);
                run.put("output", "合规校验" + (passed ? "通过" : "未通过")
                        + "：段数 8 ≤ " + segLimit + "（" + ruleRef + "），限价在 [50, 1500] 区间内");
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "规则引擎按 rule_config 实时取数（FR-PD-01 政策联动），仲裁最高优先（SRS R1）");
                break;
            case "quote":
                run.put("inputSnapshot", "预测结果 + 发电成本曲线 + 风险偏好 + 情报修正 " + String.format("%.2f", sentiment));
                run.put("output", "分段报价建议：基准价上浮 3%（"
                        + Math.round(base * 1.03 * (1 + sentiment * 0.02) * 100.0) / 100.0 + " 元/MWh），申报 8 段");
                run.put("outputPrice", Math.round(base * 1.03 * (1 + sentiment * 0.02) * 100.0) / 100.0);
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "分段聚合算法 + 情景模拟（基准/保守/激进），FR-DM-04 报价方案生成，"
                                + "舆情评分通过报价系数影响申报价（情报→决策联动）");
                break;
            case "risk":
                run.put("inputSnapshot", "报价方案 + 历史出清波动率");
                run.put("output", "CVaR 测算：日内最大回撤风险 " + Math.round((4.5 + seed % 3) * 10.0) / 10.0 + "%，建议限价 "
                        + Math.round(base * 1.35 * 100.0) / 100.0 + " 元/MWh（高于报价上浮，触发价差仲裁）");
                run.put("outputPrice", Math.round(base * 1.35 * 100.0) / 100.0);
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "蒙特卡洛情景压力测试（FR-DM-05），CVaR(95%) 口径见数据字典 7.4");
                break;
            case "settlement":
                run.put("inputSnapshot", "申报方案 + 结算规则（偏差考核）");
                run.put("output", "结算影响测算：预估结算收益 "
                        + Math.round((base * 0.12) * 100.0) / 100.0 + " 万元，偏差考核风险低");
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "结算规则引擎（FR-RS-02），偏差考核阈值取 rule_config RULE-DEV-ASSESS");
                break;
            default: // review
                run.put("inputSnapshot", "其余智能体输出汇总");
                run.put("output", "复盘建议：关注夜间低负荷时段价格风险，策略表现待成交后回填评估");
                ((Map<String, Object>) run.get("reasoning")).put("basis",
                        "复盘知识库（FR-RS-01），结论可回流策略库");
                break;
        }
        // 置信度：激进情景下风险智能体低置信度（<0.7 触发强制人工关注，SRS R3）
        double confidence = 0.82 + (seed % 13) / 100.0;
        if ("risk".equals(code) && "aggressive".equals(scenario)) {
            confidence = 0.65;
        }
        run.put("confidence", Math.round(confidence * 100.0) / 100.0);
        run.put("elapsedMs", 80 + (int) (seed % 120));
        run.put("status", "success");
        // 模型接入：注册表绑定在线模型 → 真实推理（ModelService.inference）；失败/未绑定 → 确定性算法回退
        applyModelBinding(code, run);
        // 算法匹配（V2.2 产品化）：按智能体类目应用注册表最新 enabled 算法并标注版本（替换算法即生效）
        attachAlgorithm(code, run);
        return run;
    }

    /** 智能体 → 算法类目映射（算法注册表按类目匹配决策过程） */
    private static final Map<String, String> AGENT_ALG_CATEGORY = new LinkedHashMap<>();

    static {
        AGENT_ALG_CATEGORY.put("forecast", "forecast");
        AGENT_ALG_CATEGORY.put("market", "market_analysis");
        AGENT_ALG_CATEGORY.put("quote", "quote_strategy");
        AGENT_ALG_CATEGORY.put("risk", "risk_measure");
        AGENT_ALG_CATEGORY.put("compliance", "rule_engine");
        AGENT_ALG_CATEGORY.put("settlement", "settlement");
        AGENT_ALG_CATEGORY.put("review", "review");
    }

    /**
     * 算法应用与执行（V2.2 产品化 + P3 插件化执行）：algorithm_registry 按类目匹配最新 enabled 算法，
     * 调用注册算法绑定的 SPI 执行器（缺省按类目）真实计算并留痕；
     * 客户注册新版本/绑定新执行器并启用即完成算法替换，后续会话按新算法执行；
     * 无匹配时保持原确定性逻辑（不标注）；执行器未匹配时仅标注不执行。
     */
    private void attachAlgorithm(String code, Map<String, Object> run) {
        String category = AGENT_ALG_CATEGORY.get(code);
        if (category == null) {
            return;
        }
        Map<String, Object> alg = algorithmService.matchAlgorithm(category);
        if (alg == null) {
            return;
        }
        Map<String, Object> algInfo = new LinkedHashMap<>();
        algInfo.put("algCode", alg.get("algCode"));
        algInfo.put("algName", alg.get("algName"));
        algInfo.put("category", category);
        algInfo.put("version", alg.get("version"));
        algInfo.put("spiKey", alg.get("spiKey"));
        algInfo.put("params", alg.get("paramsSchema"));
        run.put("algorithm", algInfo);
        String basis = String.valueOf(((Map<String, Object>) run.get("reasoning")).getOrDefault("basis", ""))
                + "（算法应用：" + alg.get("algName") + " " + alg.get("version")
                + "，参数 " + alg.get("paramsSchema") + "）";
        // P3：SPI 插件化执行（注册算法绑定执行器优先，类目兜底）
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("input", run.getOrDefault("output", ""));
        context.put("confidence", run.get("confidence"));
        context.put("agentCode", code);
        try {
            Map<String, Object> execution = algorithmSpiRegistry.execute(category,
                    alg.get("spiKey") == null ? null : String.valueOf(alg.get("spiKey")),
                    String.valueOf(alg.get("algCode")),
                    parseJson(String.valueOf(alg.getOrDefault("paramsSchema", "{}"))), context);
            if (execution != null) {
                algInfo.put("execution", execution);
                basis += "；执行：" + execution.get("spiKey") + " → " + execution.get("summary");
            }
        } catch (Exception e) {
            Map<String, Object> failed = new LinkedHashMap<>();
            failed.put("spiKey", alg.get("spiKey") == null ? category : alg.get("spiKey"));
            failed.put("error", e.getMessage());
            algInfo.put("execution", failed);
            basis += "；执行失败：" + e.getMessage();
        }
        Map<String, Object> reasoning = (Map<String, Object>) run.get("reasoning");
        reasoning.put("basis", basis);
    }

    /**
     * 模型接入（PRD FR-TR-05 模型平台 → 智能体执行）：
     * 1) modelCode 绑定 model_registry 在线模型时调用 ModelService.inference（数值模型推理，mode=model）；
     * 2) llmCode 绑定 llm_model 时追加生成式 LLM 解读（mode=llm / model+llm，V2.2 产品化）；
     * 未绑定或绑定模型不可用时回退确定性算法（mode=mock + modelFallback=true，可解释降级）。
     */
    @SuppressWarnings("unchecked")
    private void applyModelBinding(String code, Map<String, Object> run) {
        AgentRegistry reg = agentRegistryMapper.selectOne(new LambdaQueryWrapper<AgentRegistry>()
                .eq(AgentRegistry::getAgentCode, code).last("LIMIT 1"));
        Map<String, Object> config = reg == null ? new LinkedHashMap<>() : parseJson(reg.getModelConfig());
        String modelCode = String.valueOf(config.getOrDefault("modelCode", ""));
        String llmCode = String.valueOf(config.getOrDefault("llmCode", ""));
        boolean modelBound = StrUtils.isNotBlank(modelCode) && !"null".equals(modelCode);
        boolean llmBound = StrUtils.isNotBlank(llmCode) && !"null".equals(llmCode);
        if (!modelBound && !llmBound) {
            run.put("mode", "mock");
            return;
        }
        if (modelBound) {
            try {
                Map<String, Object> input = new LinkedHashMap<>();
                input.put("input", run.getOrDefault("output", ""));
                input.put("features", new String[]{"load_history", "weather", "price_history"});
                Map<String, Object> resp = modelService.inference(modelCode, input, null);
                Map<String, Object> modelOut = (Map<String, Object>) resp.get("output");
                run.put("mode", "model");
                run.put("modelVersion", resp.get("modelVersion"));
                // 模型置信度覆盖智能体置信度（模型指标为权威信号）；预测序列摘要随输出留痕
                if (resp.get("confidence") instanceof Number) {
                    run.put("confidence", resp.get("confidence"));
                }
                Object seriesStats = modelOut.get("seriesStats");
                String seriesDesc = seriesStats == null ? "" : "；预测序列 96 点（均值 "
                        + String.valueOf(((Map<?, ?>) seriesStats).get("avg")) + "，峰 "
                        + String.valueOf(((Map<?, ?>) seriesStats).get("peak")) + "，谷 "
                        + String.valueOf(((Map<?, ?>) seriesStats).get("trough")) + " 元/MWh）";
                run.put("output", "【模型接入 " + modelCode + " " + resp.get("modelVersion")
                        + "，置信度 " + resp.get("confidence") + "】"
                        + modelOut.getOrDefault("summary", "") + seriesDesc + "；领域结论：" + run.getOrDefault("output", ""));
                run.put("elapsedMs", ((Number) run.get("elapsedMs")).intValue()
                        + ((Number) resp.getOrDefault("latencyMs", 0)).intValue());
                Map<String, Object> reasoning = (Map<String, Object>) run.get("reasoning");
                reasoning.put("basis", String.valueOf(reasoning.getOrDefault("basis", ""))
                        + "（执行模式：模型推理 " + modelCode + "，版本 " + resp.get("modelVersion") + "）");
            } catch (ServiceException e) {
                run.put("mode", "mock");
                run.put("modelFallback", true);
                Map<String, Object> reasoning = (Map<String, Object>) run.get("reasoning");
                reasoning.put("basis", String.valueOf(reasoning.getOrDefault("basis", ""))
                        + "；绑定模型不可用，回退确定性算法（" + e.getMessage() + "）");
            }
        }
        if (llmBound) {
            try {
                Map<String, Object> resp = llmModelService.inference(llmCode,
                        String.valueOf(run.getOrDefault("output", "")), null);
                String mode = "model".equals(run.get("mode")) ? "model+llm" : "llm";
                run.put("mode", mode);
                run.put("llmModel", resp.get("modelName"));
                run.put("llmSimulate", resp.get("simulate"));
                run.put("llmGateway", resp.get("gateway"));
                run.put("output", String.valueOf(run.getOrDefault("output", ""))
                        + "；LLM 解读：" + resp.get("content"));
                run.put("elapsedMs", ((Number) run.get("elapsedMs")).intValue()
                        + ((Number) resp.getOrDefault("latencyMs", 0)).intValue());
                Map<String, Object> reasoning = (Map<String, Object>) run.get("reasoning");
                reasoning.put("basis", String.valueOf(reasoning.getOrDefault("basis", ""))
                        + "（执行模式：LLM 解读 " + llmCode + "，" + resp.get("modelName") + "）");
            } catch (ServiceException e) {
                Map<String, Object> reasoning = (Map<String, Object>) run.get("reasoning");
                reasoning.put("basis", String.valueOf(reasoning.getOrDefault("basis", ""))
                        + "；LLM 不可用，回退确定性解读（" + e.getMessage() + "）");
            }
        }
    }

    /**
     * 情报→决策联动（FR-INT-04）：近 24h 情报流舆情/供需评分（[-1,1]，正=看涨）。
     * 评分规则：opinion 标题关键词（看好/复苏/攀升 → 正；收紧/收窄/风险/预警 → 负）、
     * supply_demand（偏紧/紧缺 → 正）、weather（来水偏丰 → 负）；按重要度加权归一。
     * 近 24h 无情报时回退最新 50 条（保证评分输入非空，窗口标注可见）。
     */
    private Map<String, Object> intelImpact() {
        Date since = new Date(System.currentTimeMillis() - INTEL_WINDOW_MS);
        List<IntelNews> news = intelNewsMapper.selectList(new LambdaQueryWrapper<IntelNews>()
                .ge(IntelNews::getPublishedAt, since)
                .orderByDesc(IntelNews::getPublishedAt)
                .last("LIMIT 50"));
        String window = "近24h";
        if (news.isEmpty()) {
            window = "回退最新50条";
            news = intelNewsMapper.selectList(new LambdaQueryWrapper<IntelNews>()
                    .orderByDesc(IntelNews::getPublishedAt)
                    .last("LIMIT 50"));
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("count", news.size());
        resp.put("window", window);
        if (news.isEmpty()) {
            resp.put("score", 0.0);
            resp.put("impact", "情报库为空，无评分修正");
            return resp;
        }
        double score = 0;
        double weightSum = 0;
        List<String> impacts = new ArrayList<>();
        for (IntelNews n : news) {
            double w = "high".equals(n.getImportance()) ? 1.0
                    : "medium".equals(n.getImportance()) ? 0.5 : 0.2;
            String text = (n.getTitle() == null ? "" : n.getTitle())
                    + (n.getContent() == null ? "" : n.getContent());
            double s = 0;
            if (text.contains("看好") || text.contains("复苏") || text.contains("攀升")) {
                s += 0.6;
            }
            if (text.contains("收紧") || text.contains("收窄") || text.contains("风险") || text.contains("预警")) {
                s -= 0.6;
            }
            if (text.contains("偏紧") || text.contains("紧缺")) {
                s += 0.4;
            }
            if (text.contains("偏丰")) {
                s -= 0.3;
            }
            score += s * w;
            weightSum += w;
            if (s != 0 && impacts.size() < 3) {
                impacts.add(n.getTitle());
            }
        }
        double sentiment = weightSum == 0 ? 0 : Math.max(-1, Math.min(1, score / weightSum));
        resp.put("score", Math.round(sentiment * 100.0) / 100.0);
        resp.put("impact", impacts.isEmpty() ? "无显著影响情报" : String.join("；", impacts));
        return resp;
    }

    /** 智能体运行记录持久化（agent_run：输入快照/输出/置信度/耗时，SRS FR-DM-02） */
    private void recordAgentRun(String sessionNo, int idx, Map<String, Object> run, String runSuffix) {
        AgentRun ar = new AgentRun();
        ar.setId(SnowflakeIdGenerator.nextId());
        ar.setRunId("AR-" + sessionNo + "-" + run.get("agentCode") + "-" + idx + runSuffix);
        ar.setAgentCode(String.valueOf(run.get("agentCode")));
        ar.setSessionId(sessionNo);
        ar.setInputSnapshot(toJson(run.getOrDefault("inputSnapshot", "")));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("summary", run.getOrDefault("output", ""));
        if (run.containsKey("outputPrice")) {
            out.put("outputPrice", run.get("outputPrice"));
        }
        if (run.containsKey("mode")) {
            out.put("mode", run.get("mode"));
        }
        if (run.containsKey("modelVersion")) {
            out.put("modelVersion", run.get("modelVersion"));
        }
        if (run.containsKey("modelFallback")) {
            out.put("modelFallback", run.get("modelFallback"));
        }
        ar.setOutput(toJson(out));
        ar.setConfidence(BigDecimal.valueOf(((Number) run.get("confidence")).doubleValue())
                .setScale(2, RoundingMode.HALF_UP));
        ar.setReasoning(toJson(run.getOrDefault("reasoning", new LinkedHashMap<>())));
        ar.setElapsedMs(((Number) run.get("elapsedMs")).intValue());
        ar.setStatus(String.valueOf(run.get("status")));
        agentRunMapper.insert(ar);
    }

    /** 情景系数：baseline 0 / conservative -8% / aggressive +8% */
    private double scenarioFactor(String scenario) {
        switch (scenario) {
            case "conservative":
                return -30;
            case "aggressive":
                return 30;
            default:
                return 0;
        }
    }

    private Map<String, Object> buildStrategy(String sessionType, String scenario, long seed,
                                              Map<String, Object> quoteOut, Map<String, Object> riskOut,
                                              List<Map<String, Object>> runs) {
        double base = 380 + ((seed / 3600000) % 200) + scenarioFactor(scenario);
        Map<String, Object> strategy = new LinkedHashMap<>();
        strategy.put("sessionType", sessionType);
        strategy.put("scenario", scenario);
        switch (sessionType) {
            case "spot_quote":
                List<Map<String, Object>> segments = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    Map<String, Object> seg = new LinkedHashMap<>();
                    seg.put("segmentNo", i + 1);
                    seg.put("price", Math.round((base * (0.92 + i * 0.03)) * 100.0) / 100.0);
                    seg.put("volume", Math.round(50 + (seed + i * 7) % 60));
                    segments.add(seg);
                }
                strategy.put("quoteSegments", segments);
                strategy.put("expectedRevenue", Math.round(base * 0.18 * 100.0) / 100.0);
                break;
            case "joint_optimize":
                strategy.put("markets", new String[]{"中长期", "省内现货", "省间现货"});
                strategy.put("allocation", "中长期 60% / 省内现货 30% / 省间现货 10%");
                strategy.put("expectedRevenue", Math.round(base * 0.25 * 100.0) / 100.0);
                break;
            default: // rolling
                List<Map<String, Object>> adjustments = new ArrayList<>();
                adjustments.add(adjust("T01-T08", "晚间高峰增持", Math.round(20 + seed % 30), base * 1.05));
                adjustments.add(adjust("T33-T48", "午间谷段减持", Math.round(15 + seed % 20), base * 0.92));
                strategy.put("adjustments", adjustments);
                strategy.put("expectedRevenue", Math.round(base * 0.15 * 100.0) / 100.0);
                break;
        }
        strategy.put("riskMetrics", riskOut == null ? "CVaR 5.2%" : riskOut.get("output"));

        // 综合决策：加权聚合各智能体置信度（SRS FR-DM-02 加权综合评分）
        double avgConf = runs.stream()
                .filter(r -> !"timeout".equals(r.get("status")))
                .mapToDouble(r -> ((Number) r.get("confidence")).doubleValue())
                .average().orElse(0);
        strategy.put("confidenceAvg", Math.round(avgConf * 100.0) / 100.0);

        // 低置信度标注：<0.7 强制人工关注（SRS R3）
        List<String> lowConf = new ArrayList<>();
        for (Map<String, Object> r : runs) {
            if (((Number) r.get("confidence")).doubleValue() < CONFIDENCE_LOW) {
                lowConf.add(String.valueOf(r.get("agentCode")));
            }
        }
        if (!lowConf.isEmpty()) {
            strategy.put("attentionRequired", true);
            strategy.put("lowConfidenceAgents", lowConf);
            strategy.put("attentionNote", "以下智能体置信度 < " + CONFIDENCE_LOW + "，请人工重点复核："
                    + String.join("/", lowConf));
        } else {
            strategy.put("attentionRequired", false);
        }

        boolean degraded = runs.stream().anyMatch(r -> "timeout".equals(r.get("status")));
        strategy.put("degraded", degraded);
        strategy.put("note", "策略由多智能体协同生成（" + ORCHESTRATOR_VERSION + "），人工确认后方可执行（辅助决策定位）"
                + (degraded ? "；本次编排存在降级智能体，已标记可补跑" : ""));
        return strategy;
    }

    private Map<String, Object> adjust(String period, String action, double volume, double price) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("period", period);
        m.put("action", action);
        m.put("volume", volume);
        m.put("price", Math.round(price * 100.0) / 100.0);
        return m;
    }

    // ---------- 详情 / 人机协同 ----------

    /**
     * 决策会话列表（M7 移动端策略确认入口：按人审状态筛选，分页倒序）
     */
    public Map<String, Object> listSessions(String humanReviewStatus, long pageNo, long pageSize) {
        LambdaQueryWrapper<DecisionSession> wrapper = new LambdaQueryWrapper<>();
        if (StrUtils.isNotBlank(humanReviewStatus)) {
            wrapper.eq(DecisionSession::getHumanReviewStatus, humanReviewStatus);
        }
        wrapper.orderByDesc(DecisionSession::getCreatedAt);
        Page<DecisionSession> page = decisionSessionMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<Map<String, Object>> list = new ArrayList<>();
        for (DecisionSession s : page.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sessionId", String.valueOf(s.getId()));
            item.put("sessionNo", s.getSessionNo());
            item.put("sessionType", s.getSessionType());
            item.put("tradeDate", s.getTradeDate());
            item.put("humanReviewStatus", s.getHumanReviewStatus());
            item.put("reviewedBy", s.getReviewedBy());
            item.put("reviewedAt", s.getReviewedAt());
            list.add(item);
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("list", list);
        resp.put("pageNo", page.getCurrent());
        resp.put("pageSize", page.getSize());
        resp.put("total", page.getTotal());
        return resp;
    }

    public DecisionSessionView getSession(Long sessionId) {
        DecisionSession s = getEntity(sessionId);
        Map<String, Object> strategy = parseJson(s.getFinalStrategy());
        return toView(s, parseJsonList(s.getEvidenceChain()), strategy);
    }

    public void confirmSession(Long sessionId) {
        DecisionSession s = getEntity(sessionId);
        if (!"pending".equals(s.getHumanReviewStatus())) {
            throw new ServiceException("会话已处理（" + s.getHumanReviewStatus() + "），不可重复确认");
        }
        DecisionSession update = new DecisionSession();
        update.setId(sessionId);
        update.setHumanReviewStatus("confirmed");
        update.setReviewedBy(securityUtils.getUsername());
        update.setReviewedAt(new Date());
        decisionSessionMapper.updateById(update);
    }

    /** 修改策略：依据必填；修改量超阈值（15%）必须双人复核（FR-DM-05） */
    public void modifySession(Long sessionId, DecisionModifyRequest req) {
        if (req.getModifications() == null || req.getModifications().isEmpty()) {
            throw new ServiceException("修改明细不能为空");
        }
        if (StrUtils.isBlank(req.getReason())) {
            throw new ServiceException("修改依据必填（FR-DM-05）");
        }
        DecisionSession s = getEntity(sessionId);
        if (!"pending".equals(s.getHumanReviewStatus()) && !"confirmed".equals(s.getHumanReviewStatus())) {
            throw new ServiceException("会话状态不允许修改：" + s.getHumanReviewStatus());
        }
        double deltaRatio = estimateDeltaRatio(req.getModifications());
        if (deltaRatio > REVIEW2_THRESHOLD && StrUtils.isBlank(req.getSecondReviewer())) {
            throw new ServiceException("修改量超过阈值 " + REVIEW2_THRESHOLD + "%，必须填写复核人（双人复核）");
        }
        // 更新最终策略（登记修改明细与依据；实体更新走 JSONB typeHandler）
        Map<String, Object> strategy = parseJson(s.getFinalStrategy());
        strategy.put("modified", req.getModifications());
        strategy.put("modifyReason", req.getReason());
        strategy.put("modifyDeltaRatio", Math.round(deltaRatio * 10.0) / 10.0);
        DecisionSession update = new DecisionSession();
        update.setId(sessionId);
        update.setHumanReviewStatus("modified");
        update.setReviewedBy(securityUtils.getUsername());
        update.setReviewedAt(new Date());
        update.setModifyReason(req.getReason());
        update.setReviewer2(req.getSecondReviewer());
        update.setFinalStrategy(toJson(strategy));
        decisionSessionMapper.updateById(update);
    }

    /** 依据链全量回溯（Agent 输入/输出/置信度 + 冲突仲裁） */
    public Map<String, Object> getEvidence(Long sessionId) {
        DecisionSession s = getEntity(sessionId);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("sessionNo", s.getSessionNo());
        resp.put("orchestratorVersion", s.getOrchestratorVersion());
        resp.put("agents", parseJsonList(s.getEvidenceChain()));
        resp.put("conflicts", parseJsonList(s.getConflictRecords()));
        resp.put("humanReviewStatus", s.getHumanReviewStatus());
        return resp;
    }

    /** 人审驳回（FR-DM-05）：pending/confirmed → rejected，原因必填（复用 modify_reason 列，无需 DDL 变更） */
    public void rejectSession(Long sessionId, String reason) {
        if (StrUtils.isBlank(reason)) {
            throw new ServiceException("驳回原因必填（FR-DM-05）");
        }
        DecisionSession s = getEntity(sessionId);
        if (!"pending".equals(s.getHumanReviewStatus()) && !"confirmed".equals(s.getHumanReviewStatus())) {
            throw new ServiceException("会话状态不允许驳回：" + s.getHumanReviewStatus());
        }
        DecisionSession update = new DecisionSession();
        update.setId(sessionId);
        update.setHumanReviewStatus("rejected");
        update.setReviewedBy(securityUtils.getUsername());
        update.setReviewedAt(new Date());
        update.setModifyReason(reason);
        decisionSessionMapper.updateById(update);
    }

    /** 修改量占比粗估：量字段绝对值合计 / 策略原量合计（无原量时按 1000 MWh 基准） */
    private double estimateDeltaRatio(List<Map<String, Object>> modifications) {
        double totalDelta = 0;
        for (Map<String, Object> m : modifications) {
            Object v = m.get("volume");
            if (v instanceof Number) {
                totalDelta += Math.abs(((Number) v).doubleValue());
            }
        }
        return totalDelta / 1000.0 * 100.0;
    }

    private DecisionSession getEntity(Long sessionId) {
        DecisionSession s = decisionSessionMapper.selectOne(new LambdaQueryWrapper<DecisionSession>()
                .eq(DecisionSession::getId, sessionId));
        if (s == null) {
            throw new ServiceException("决策会话不存在");
        }
        return s;
    }

    private DecisionSessionView toView(DecisionSession s, List<Map<String, Object>> runs,
                                       Map<String, Object> strategy) {
        DecisionSessionView v = new DecisionSessionView();
        v.setSessionId(String.valueOf(s.getId()));
        v.setSessionNo(s.getSessionNo());
        v.setSessionType(s.getSessionType());
        v.setTradeDate(s.getTradeDate());
        v.setStatus("completed");
        v.setFinalStrategy(strategy);
        v.setAgents(parseStringList(s.getAgents()));
        v.setHumanReviewStatus(s.getHumanReviewStatus());
        v.setReviewedBy(s.getReviewedBy());
        v.setModifyReason(s.getModifyReason());
        v.setReviewer2(s.getReviewer2());
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("agentCount", runs.size());
        summary.put("confidenceAvg", Math.round(runs.stream()
                .filter(r -> !"timeout".equals(r.get("status")))
                .mapToDouble(r -> ((Number) r.getOrDefault("confidence", 0)).doubleValue())
                .average().orElse(0) * 100.0) / 100.0);
        summary.put("degraded", runs.stream().anyMatch(r -> "timeout".equals(r.get("status"))));
        v.setEvidenceSummary(summary);
        return v;
    }

    private List<Map<String, Object>> parseJsonList(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Map.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castList(Object o) {
        return (List<Map<String, Object>>) o;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object o) {
        return (Map<String, Object>) o;
    }

    private List<String> parseStringList(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructMapType(Map.class, String.class, Object.class));
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败：" + e.getMessage());
        }
    }
}

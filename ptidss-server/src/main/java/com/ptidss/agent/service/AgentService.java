package com.ptidss.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.agent.domain.AgentRegistry;
import com.ptidss.agent.domain.AgentRun;
import com.ptidss.agent.mapper.AgentRegistryMapper;
import com.ptidss.agent.mapper.AgentRunMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.model.domain.LlmModel;
import com.ptidss.model.domain.ModelRegistry;
import com.ptidss.model.mapper.LlmModelMapper;
import com.ptidss.model.mapper.ModelRegistryMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体管理（DDL 7.1/7.2 agent_registry + agent_run；PRD FR-TR-05 智能体管理、SRS FR-DM-02 契约化）
 * 功能：注册表懒种子（7 大智能体）、运行记录查询、启停维护、效果评估（成功率/平均置信度/平均耗时）
 * 说明：智能体执行由 DecisionService 编排并写入 agent_run（本服务只读查询 + 注册管理）
 */
@Service
public class AgentService {

    private static final String[] AGENT_CODES = {
            "forecast", "market", "quote", "risk", "compliance", "settlement", "review"};

    private final AgentRegistryMapper agentRegistryMapper;
    private final AgentRunMapper agentRunMapper;
    private final ModelRegistryMapper modelRegistryMapper;
    private final LlmModelMapper llmModelMapper;
    private final ObjectMapper objectMapper;

    public AgentService(AgentRegistryMapper agentRegistryMapper, AgentRunMapper agentRunMapper,
                        ModelRegistryMapper modelRegistryMapper, LlmModelMapper llmModelMapper,
                        ObjectMapper objectMapper) {
        this.agentRegistryMapper = agentRegistryMapper;
        this.agentRunMapper = agentRunMapper;
        this.modelRegistryMapper = modelRegistryMapper;
        this.llmModelMapper = llmModelMapper;
        this.objectMapper = objectMapper;
    }

    /** 注册表懒种子（与 07_seed_data.sql 无冲突：表空时写入 7 大智能体，幂等） */
    public void ensureAgents() {
        Long count = agentRegistryMapper.selectCount(new LambdaQueryWrapper<AgentRegistry>());
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"forecast", "预测智能体", "负荷/新能源出力/电价 96 点预测，输出置信区间（FR-PD-03）",
                        "{\"features\":[\"load_history\",\"weather\",\"price_history\"],\"points\":96}",
                        "{\"forecast\":\"series\",\"confidence\":\"0-1\",\"band\":\"90%\"}",
                        "v0.1.0", "{\"modelCode\":\"price\",\"framework\":\"pytorch\"}"},
                {"market", "行情智能体", "市场情报研判：供需比/价格趋势/省间价差（SRS FR-DM-01 编排首步）",
                        "{\"sources\":[\"clearing\",\"supply_demand\",\"inter_province\"]}",
                        "{\"trend\":\"string\",\"supplyDemandRatio\":\"number\",\"keyEvents\":\"array\"}",
                        "v0.1.0", "{\"modelCode\":\"market\",\"framework\":\"rule+llm\"}"},
                {"quote", "报价智能体", "分段量价申报建议（FR-TR-03；含情景模拟）",
                        "{\"forecast\":\"series\",\"costCurve\":\"series\",\"riskPreference\":\"string\"}",
                        "{\"segments\":\"array\",\"expectedRevenue\":\"number\"}",
                        "v0.1.0", "{\"modelCode\":\"quote\",\"framework\":\"rule\"}"},
                {"risk", "风险智能体", "CVaR/最大回撤/偏差考核风险度量与限价建议（FR-DM-05 情景压力）",
                        "{\"quotePlan\":\"array\",\"volatility\":\"series\"}",
                        "{\"cvar\":\"number\",\"maxDrawdown\":\"number\",\"limitPrice\":\"number\"}",
                        "v0.1.0", "{\"modelCode\":\"risk\",\"framework\":\"mc-simulation\"}"},
                {"compliance", "合规智能体", "申报段数/限价/持仓比例合规校验（rule_config 实时取数，FR-DM-02 仲裁最高优先）",
                        "{\"plan\":\"array\",\"ruleConfig\":\"active-rules\"}",
                        "{\"passed\":\"boolean\",\"violations\":\"array\"}",
                        "v0.1.0", "{\"modelCode\":\"rule_engine\",\"framework\":\"drools\"}"},
                {"settlement", "结算智能体", "结算影响测算与偏差考核预评估（FR-RS-02）",
                        "{\"plan\":\"array\",\"settlementRules\":\"object\"}",
                        "{\"impact\":\"number\",\"deviationRisk\":\"string\"}",
                        "v0.1.0", "{\"modelCode\":\"settlement\",\"framework\":\"rule\"}"},
                {"review", "复盘智能体", "决策-结果-原因-改进闭环复盘，沉淀策略库（FR-RS-01）",
                        "{\"session\":\"object\",\"results\":\"array\"}",
                        "{\"conclusion\":\"string\",\"feedback\":\"object\"}",
                        "v0.1.0", "{\"modelCode\":\"review\",\"framework\":\"llm\"}"},
        };
        long id = 7001;
        for (String[] s : seeds) {
            AgentRegistry a = new AgentRegistry();
            a.setId(id++);
            a.setAgentCode(s[0]);
            a.setAgentName(s[1]);
            a.setRole(s[2]);
            a.setInputSchema(s[3]);
            a.setOutputSchema(s[4]);
            a.setVersion(s[5]);
            a.setModelConfig(s[6]);
            a.setStatus("active");
            agentRegistryMapper.insert(a);
        }
    }

    /** 智能体注册列表（版本化；供智能体管理页） */
    public List<Map<String, Object>> registry() {
        ensureAgents();
        List<AgentRegistry> list = agentRegistryMapper.selectList(new LambdaQueryWrapper<AgentRegistry>()
                .orderByAsc(AgentRegistry::getAgentCode));
        List<Map<String, Object>> result = new ArrayList<>();
        for (AgentRegistry a : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(a.getId()));
            item.put("agentCode", a.getAgentCode());
            item.put("agentName", a.getAgentName());
            item.put("role", a.getRole());
            item.put("inputSchema", parseJson(a.getInputSchema()));
            item.put("outputSchema", parseJson(a.getOutputSchema()));
            item.put("version", a.getVersion());
            item.put("modelConfig", parseJson(a.getModelConfig()));
            item.put("status", a.getStatus());
            result.add(item);
        }
        return result;
    }

    /** 运行记录查询（可按智能体/会话过滤；缺省最近 50 条） */
    public List<Map<String, Object>> runs(String agentCode, String sessionId, Integer limit) {
        int size = limit == null ? 50 : Math.min(Math.max(limit, 1), 200);
        LambdaQueryWrapper<AgentRun> qw = new LambdaQueryWrapper<AgentRun>()
                .eq(StrUtils.isNotBlank(agentCode), AgentRun::getAgentCode, agentCode)
                .eq(StrUtils.isNotBlank(sessionId), AgentRun::getSessionId, sessionId)
                .orderByDesc(AgentRun::getCreatedAt)
                .last("LIMIT " + size);
        List<AgentRun> list = agentRunMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (AgentRun r : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("runId", r.getRunId());
            item.put("agentCode", r.getAgentCode());
            item.put("sessionId", r.getSessionId());
            item.put("inputSnapshot", parseJson(r.getInputSnapshot()));
            item.put("output", parseJson(r.getOutput()));
            item.put("confidence", r.getConfidence());
            item.put("reasoning", parseJson(r.getReasoning()));
            item.put("elapsedMs", r.getElapsedMs());
            item.put("status", r.getStatus());
            item.put("createdAt", r.getCreatedAt());
            result.add(item);
        }
        return result;
    }

    /** 启停维护：active/disabled/maintenance（PRD FR-TR-05 智能体管理-参数调优） */
    public void updateStatus(Long id, String status) {
        if (!Arrays.asList("active", "disabled", "maintenance").contains(status)) {
            throw new ServiceException("状态仅支持 active/disabled/maintenance");
        }
        AgentRegistry a = agentRegistryMapper.selectById(id);
        if (a == null) {
            throw new ServiceException("智能体不存在");
        }
        AgentRegistry update = new AgentRegistry();
        update.setId(id);
        update.setStatus(status);
        agentRegistryMapper.updateById(update);
    }

    /**
     * 模型绑定（PRD FR-TR-05 模型平台 → 智能体接入）：将 agent_registry.model_config 绑定
     * model_registry 已有模型（modelCode，编排走数值模型推理）与 llm_model 生成式模型
     * （llmCode，编排追加 LLM 解读）；两者均可选，传空/仅空白串解绑（回退确定性算法）。
     * 绑定不存在的编码直接拒绝，防止编排静默降级。
     */
    public void bindModel(Long id, String modelCode, String llmCode) {
        AgentRegistry a = agentRegistryMapper.selectById(id);
        if (a == null) {
            throw new ServiceException("智能体不存在");
        }
        boolean modelBlank = StrUtils.isBlank(modelCode) || "null".equals(modelCode);
        boolean llmBlank = StrUtils.isBlank(llmCode) || "null".equals(llmCode);
        if (modelBlank && llmBlank) {
            AgentRegistry update = new AgentRegistry();
            update.setId(id);
            update.setModelConfig("{}");
            agentRegistryMapper.updateById(update);
            return;
        }
        Map<String, Object> config = new LinkedHashMap<>();
        if (!modelBlank) {
            Long exists = modelRegistryMapper.selectCount(new LambdaQueryWrapper<ModelRegistry>()
                    .eq(ModelRegistry::getModelCode, modelCode));
            if (exists == null || exists == 0) {
                throw new ServiceException("模型不存在：" + modelCode + "（model_registry 无此编码，可先注册模型）");
            }
            config.put("modelCode", modelCode);
        }
        if (!llmBlank) {
            Long llmExists = llmModelMapper.selectCount(new LambdaQueryWrapper<LlmModel>()
                    .eq(LlmModel::getModelCode, llmCode));
            if (llmExists == null || llmExists == 0) {
                throw new ServiceException("LLM 模型不存在：" + llmCode + "（llm_model 无此编码，可先在模型平台注册）");
            }
            config.put("llmCode", llmCode);
        }
        AgentRegistry update = new AgentRegistry();
        update.setId(id);
        update.setModelConfig(toJson(config));
        agentRegistryMapper.updateById(update);
    }

    /** 效果评估：按智能体聚合运行次数/成功率/平均置信度/平均耗时（PRD FR-TR-05 效果评估） */
    public List<Map<String, Object>> metrics() {
        List<AgentRun> runs = agentRunMapper.selectList(new LambdaQueryWrapper<AgentRun>()
                .orderByAsc(AgentRun::getAgentCode));
        Map<String, List<AgentRun>> byAgent = new LinkedHashMap<>();
        for (AgentRun r : runs) {
            byAgent.computeIfAbsent(r.getAgentCode(), k -> new ArrayList<>()).add(r);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (String code : AGENT_CODES) {
            List<AgentRun> agentRuns = byAgent.getOrDefault(code, new ArrayList<>());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("agentCode", code);
            item.put("runCount", agentRuns.size());
            if (!agentRuns.isEmpty()) {
                long success = agentRuns.stream().filter(r -> "success".equals(r.getStatus())).count();
                item.put("successRate", BigDecimal.valueOf(success * 100.0 / agentRuns.size())
                        .setScale(1, RoundingMode.HALF_UP));
                item.put("avgConfidence", BigDecimal.valueOf(agentRuns.stream()
                        .mapToDouble(r -> r.getConfidence() == null ? 0 : r.getConfidence().doubleValue())
                        .average().orElse(0)).setScale(2, RoundingMode.HALF_UP));
                item.put("avgElapsedMs", Math.round(agentRuns.stream()
                        .mapToLong(r -> r.getElapsedMs() == null ? 0 : r.getElapsedMs())
                        .average().orElse(0)));
            } else {
                item.put("successRate", null);
                item.put("avgConfidence", null);
                item.put("avgElapsedMs", null);
            }
            result.add(item);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        if (StrUtils.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, Map.class);
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

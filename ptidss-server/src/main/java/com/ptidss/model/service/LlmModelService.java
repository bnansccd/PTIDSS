package com.ptidss.model.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.model.domain.LlmModel;
import com.ptidss.model.gateway.LlmGatewayClient;
import com.ptidss.model.gateway.LlmGatewayException;
import com.ptidss.model.mapper.LlmModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 模型配置与推理网关（DDL 10.2 llm_model；V2.2 产品化：智能体可关联的生成式模型；
 * P2 真实 LLM HTTP 通道：gateway-enabled 开启且 endpoint+apiKey 就绪时走 OpenAI 兼容真实调用，
 * 调用失败自动降级内置模拟推理（决策不中断），未就绪时直接内置模拟）
 * 推理说明：endpoint 为空或 provider=local 走内置模拟推理网关（确定性文本，可解释）。
 */
@Slf4j
@Service
public class LlmModelService {

    private static final String[] PROVIDERS = {"deepseek", "glm", "qwen", "openai-compatible", "local"};

    /** 真实供应商通道开关（P2 可配置）：开启后 endpoint+apiKeyRef 就绪的模型走 OpenAI 兼容 HTTP 调用 */
    @Value("${ptidss.llm.gateway-enabled:false}")
    private boolean gatewayEnabled;

    private final LlmModelMapper llmModelMapper;
    private final LlmGatewayClient llmGatewayClient;
    private final Environment environment;

    public LlmModelService(LlmModelMapper llmModelMapper, LlmGatewayClient llmGatewayClient,
                           Environment environment) {
        this.llmModelMapper = llmModelMapper;
        this.llmGatewayClient = llmGatewayClient;
        this.environment = environment;
    }

    /** 懒种子（与 10_platform_config.sql 种子一致，表空时写入 3 条，幂等） */
    public void ensureLlmModels() {
        Long count = llmModelMapper.selectCount(new LambdaQueryWrapper<LlmModel>());
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"deepseek-v3", "DeepSeek V3 对话模型", "deepseek",
                        "https://api.deepseek.com/v1/chat/completions", "deepseek-chat", "0.70", "2048", "LLM_API_KEY_DEEPSEEK"},
                {"glm-4", "智谱 GLM-4 对话模型", "glm",
                        "https://open.bigmodel.cn/api/paas/v4/chat/completions", "glm-4", "0.60", "2048", "LLM_API_KEY_GLM"},
                {"qwen-plus", "通义千问 Plus 对话模型", "qwen",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", "qwen-plus", "0.70", "2048", "LLM_API_KEY_QWEN"},
        };
        long id = 91001;
        for (String[] s : seeds) {
            LlmModel m = new LlmModel();
            m.setId(id++);
            m.setModelCode(s[0]);
            m.setModelName(s[1]);
            m.setProvider(s[2]);
            m.setEndpoint(s[3]);
            m.setBaseModel(s[4]);
            m.setTemperature(new BigDecimal(s[5]));
            m.setMaxTokens(Integer.valueOf(s[6]));
            m.setApiKeyRef(s[7]);
            m.setStatus("enabled");
            llmModelMapper.insert(m);
        }
    }

    /** 模型列表（配置管理页） */
    public List<Map<String, Object>> listModels() {
        ensureLlmModels();
        List<LlmModel> list = llmModelMapper.selectList(new LambdaQueryWrapper<LlmModel>()
                .orderByAsc(LlmModel::getModelCode));
        List<Map<String, Object>> result = new ArrayList<>();
        for (LlmModel m : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(m.getId()));
            item.put("modelCode", m.getModelCode());
            item.put("modelName", m.getModelName());
            item.put("provider", m.getProvider());
            item.put("endpoint", m.getEndpoint());
            item.put("baseModel", m.getBaseModel());
            item.put("temperature", m.getTemperature());
            item.put("maxTokens", m.getMaxTokens());
            item.put("apiKeyRef", m.getApiKeyRef());
            item.put("status", m.getStatus());
            result.add(item);
        }
        return result;
    }

    /** 新增模型（编码唯一；提供商枚举校验） */
    public LlmModel createModel(String modelCode, String modelName, String provider, String endpoint,
                                String baseModel, BigDecimal temperature, Integer maxTokens,
                                String apiKeyRef, String status) {
        if (StrUtils.isBlank(modelCode) || StrUtils.isBlank(modelName) || StrUtils.isBlank(provider)) {
            throw new ServiceException("模型编码/名称/提供商不能为空");
        }
        if (!Arrays.asList(PROVIDERS).contains(provider)) {
            throw new ServiceException("提供商不合法：" + provider);
        }
        Long exists = llmModelMapper.selectCount(new LambdaQueryWrapper<LlmModel>()
                .eq(LlmModel::getModelCode, modelCode));
        if (exists != null && exists > 0) {
            throw new ServiceException("模型编码已存在：" + modelCode);
        }
        LlmModel m = new LlmModel();
        m.setModelCode(modelCode);
        m.setModelName(modelName);
        m.setProvider(provider);
        m.setEndpoint(endpoint);
        m.setBaseModel(baseModel);
        m.setTemperature(temperature == null ? new BigDecimal("0.70") : temperature);
        m.setMaxTokens(maxTokens == null ? 2048 : maxTokens);
        m.setApiKeyRef(apiKeyRef);
        m.setStatus(StrUtils.isBlank(status) ? "enabled" : status);
        llmModelMapper.insert(m);
        return m;
    }

    /** 更新模型配置（连接参数/温度/限额/状态等，客户部署适配） */
    public void updateModel(Long id, String modelName, String provider, String endpoint, String baseModel,
                            BigDecimal temperature, Integer maxTokens, String apiKeyRef, String status) {
        LlmModel exist = llmModelMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("LLM 模型不存在");
        }
        if (StrUtils.isNotBlank(provider) && !Arrays.asList(PROVIDERS).contains(provider)) {
            throw new ServiceException("提供商不合法：" + provider);
        }
        LlmModel update = new LlmModel();
        update.setId(id);
        update.setModelName(modelName);
        update.setProvider(provider);
        update.setEndpoint(endpoint);
        update.setBaseModel(baseModel);
        update.setTemperature(temperature);
        update.setMaxTokens(maxTokens);
        update.setApiKeyRef(apiKeyRef);
        update.setStatus(status);
        llmModelMapper.updateById(update);
    }

    /**
     * 推理（智能体关联调用）：模型停用/不存在直接拒绝；
     * 真实通道条件：gateway-enabled && endpoint 非空 && provider != local && apiKey 就绪
     * （apiKey 解析顺序：环境变量 LLM_API_KEY_* → 配置 ptidss.llm.api-keys.{ref}）。
     * 真实调用失败自动降级内置模拟推理（gateway=degraded + 原因标注，决策不中断）；
     * 未满足真实通道条件直接内置模拟（gateway=simulate）。
     * 返回统一契约（modelCode/modelName/provider/content/tokens/latencyMs/simulate/gateway）。
     */
    public Map<String, Object> inference(String modelCode, String prompt, BigDecimal temperature) {
        LlmModel model = llmModelMapper.selectOne(new LambdaQueryWrapper<LlmModel>()
                .eq(LlmModel::getModelCode, modelCode).last("LIMIT 1"));
        if (model == null) {
            throw new ServiceException("LLM 模型不存在：" + modelCode + "（llm_model 无此编码，可先在模型平台注册）");
        }
        if (!"enabled".equals(model.getStatus())) {
            throw new ServiceException("LLM 模型已停用：" + modelCode);
        }
        BigDecimal temp = temperature == null ? model.getTemperature() : temperature;
        int maxTok = model.getMaxTokens() == null ? 2048 : model.getMaxTokens();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("modelCode", model.getModelCode());
        resp.put("modelName", model.getModelName());
        resp.put("provider", model.getProvider());
        resp.put("baseModel", model.getBaseModel());
        resp.put("temperature", temp);
        String apiKey = resolveApiKey(model.getApiKeyRef());
        boolean realReady = gatewayEnabled && StrUtils.isNotBlank(model.getEndpoint())
                && !"local".equals(model.getProvider()) && StrUtils.isNotBlank(apiKey);
        if (!realReady) {
            fillSimulate(resp, model, prompt, temp, "simulate", null);
            return resp;
        }
        try {
            Map<String, Object> out = llmGatewayClient.chat(model.getEndpoint(), apiKey,
                    model.getBaseModel(), prompt, temp, maxTok);
            resp.put("content", out.get("content"));
            resp.put("tokens", out.get("tokens"));
            resp.put("latencyMs", out.get("latencyMs"));
            resp.put("simulate", false);
            resp.put("gateway", "real");
            return resp;
        } catch (LlmGatewayException e) {
            log.warn("LLM 外部通道调用失败，降级内置模拟推理：modelCode={}，原因={}", modelCode, e.getMessage());
            fillSimulate(resp, model, prompt, temp, "degraded", e.getMessage());
            return resp;
        }
    }

    /** 内置模拟推理（确定性文本：prompt 哈希种子 + 温度影响采样长度；gateway 标注 simulate/degraded） */
    private void fillSimulate(Map<String, Object> resp, LlmModel model, String prompt, BigDecimal temp,
                              String gateway, String degradeReason) {
        long seed = model.getModelCode().hashCode() * 31L + (prompt == null ? 0 : prompt.hashCode());
        java.util.Random random = new java.util.Random(seed);
        int tokens = 120 + random.nextInt(380);
        String degrade = gateway == null || !"degraded".equals(gateway) ? ""
                : "（外部通道不可用，已降级内置模拟：" + degradeReason + "）";
        resp.put("content", "【内置模拟推理 · " + model.getModelName() + "】对输入要点解读："
                + (prompt == null || prompt.length() < 12 ? "（无有效输入）" : prompt.substring(0, Math.min(12, prompt.length())) + "…")
                + "；结合供需平衡与价格趋势给出交易关注点"
                + String.format("，风险敞口评级 %s（温度 %.2f）",
                random.nextBoolean() ? "中" : "低", temp)
                + degrade);
        resp.put("tokens", tokens);
        resp.put("latencyMs", 320 + (int) (prompt == null ? 0 : prompt.length() % 180));
        resp.put("simulate", true);
        resp.put("gateway", gateway);
        if (degradeReason != null) {
            resp.put("degradeReason", degradeReason);
        }
    }

    /** 密钥解析：环境变量优先（LLM_API_KEY_*），其次配置 ptidss.llm.api-keys.{ref}（部署/内网联调） */
    private String resolveApiKey(String apiKeyRef) {
        if (StrUtils.isBlank(apiKeyRef)) {
            return null;
        }
        String fromEnv = System.getenv(apiKeyRef);
        if (StrUtils.isNotBlank(fromEnv)) {
            return fromEnv;
        }
        return environment.getProperty("ptidss.llm.api-keys." + apiKeyRef);
    }
}

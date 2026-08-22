package com.ptidss.model.gateway;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LLM 真实供应商通道（P2：真实 LLM HTTP 通道，OpenAI 兼容协议）
 * POST {endpoint}，Header Authorization: Bearer {apiKey}，Body {model, messages, temperature, max_tokens}；
 * 解析 choices[0].message.content 与 usage.total_tokens。连接/读超时分别为 8s/15s；
 * 调用失败抛 LlmGatewayException，由 LlmModelService 降级内置模拟推理（决策不中断）。
 */
@Slf4j
@Component
public class LlmGatewayClient {

    private static final int CONNECT_TIMEOUT_MS = 8000;
    private static final int READ_TIMEOUT_MS = 15000;

    private final RestTemplate restTemplate;

    public LlmGatewayClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * OpenAI 兼容 chat/completions 调用。
     *
     * @return {content, tokens, model, latencyMs}；非 2xx / 连接失败 / 响应结构异常抛异常
     */
    public Map<String, Object> chat(String endpoint, String apiKey, String baseModel, String prompt,
                                    BigDecimal temperature, int maxTokens) {
        long t0 = System.currentTimeMillis();
        try {
            JSONObject body = new JSONObject();
            body.put("model", baseModel);
            JSONArray messages = new JSONArray();
            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", prompt == null ? "" : prompt);
            messages.add(user);
            body.put("messages", messages);
            body.put("temperature", temperature == null ? 0.7 : temperature.doubleValue());
            body.put("max_tokens", maxTokens);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<String> resp = restTemplate.postForEntity(
                    endpoint, new HttpEntity<>(body.toJSONString(), headers), String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new LlmGatewayException("供应商返回 HTTP " + resp.getStatusCodeValue()
                        + "：" + safeBody(resp.getBody()));
            }
            JSONObject json = JSONObject.parseObject(resp.getBody());
            JSONArray choices = json == null ? null : json.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new LlmGatewayException("响应缺少 choices：" + safeBody(resp.getBody()));
            }
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message == null ? "" : String.valueOf(message.getOrDefault("content", ""));
            int tokens = 0;
            JSONObject usage = json.getJSONObject("usage");
            if (usage != null && usage.get("total_tokens") != null) {
                tokens = usage.getIntValue("total_tokens");
            }
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("content", content);
            out.put("tokens", tokens);
            out.put("model", json.getOrDefault("model", baseModel));
            out.put("latencyMs", (int) (System.currentTimeMillis() - t0));
            return out;
        } catch (LlmGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmGatewayException("外部通道调用失败：" + e.getMessage());
        }
    }

    private String safeBody(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 300 ? body.substring(0, 300) : body;
    }
}

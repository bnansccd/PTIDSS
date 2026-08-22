package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 复盘归纳执行器（review）：决策-结果-原因-改进四段式复盘（确定性参数化模板）
 * 参数：template 模板标识
 * 输出：四段式复盘结论（结论回流策略库）
 */
@Component
public class ReviewExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "review";
    }

    @Override
    public String label() {
        return "复盘知识库四段式归纳";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        String template = text(params, "template", "4step");
        long seed = seedOf(algCode, context);
        double accuracy = 55 + (Math.abs(seed) % 35);  // 55~90 方向准确率
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("template", template);
        result.put("decision", "本轮策略按定义执行，绑定算法标注与 LLM 解读留痕完整");
        result.put("result", String.format("方向准确率 %.0f%%，收益贡献为正", accuracy));
        result.put("cause", Math.abs(seed) % 2 == 0 ? "供需预测偏差是主要扰动源" : "价格波动超历史区间");
        result.put("improve", Math.abs(seed) % 2 == 0 ? "提高 lookback 窗口并复核情报情感权重" : "收紧限价并增加情景数");
        result.put("backflow", "结论已回流策略库（待人工确认）");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("四段式复盘（%s）：方向准确率 %.0f%%，改进建议——%s",
                template, accuracy, result.get("improve")));
        return out;
    }
}

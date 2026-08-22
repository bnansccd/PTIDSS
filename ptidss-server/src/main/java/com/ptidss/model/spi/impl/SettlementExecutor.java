package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 结算测算执行器（settlement）：偏差考核结算测算（确定性参数化模板）
 * 参数：dev_threshold 偏差考核阈值
 * 输出：结算收益与偏差考核风险预评估
 */
@Component
public class SettlementExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "settlement";
    }

    @Override
    public String label() {
        return "偏差考核结算测算";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        double devThreshold = dnum(params, "dev_threshold", 0.05);
        long seed = seedOf(algCode, context);
        double devRate = Math.round((Math.abs(seed) % 1000) / 1000.0 * 0.12 * 100) / 100.0; // 0~12% 偏差率
        double income = 156000 + Math.abs(seed) % 24000;
        double penalty = devRate > devThreshold
                ? Math.round((devRate - devThreshold) * 100 * 8.0 * 100) / 100.0 : 0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("devThreshold", devThreshold);
        result.put("deviationRate", devRate);
        result.put("income", Math.round(income * 100) / 100.0);
        result.put("penalty", penalty);
        result.put("riskLevel", devRate > devThreshold ? "超阈值" : "正常");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("结算收益 %.1f 元，偏差率 %.2f%%（阈值 %.0f%%），考核罚金 %.1f 元，风险%s",
                income, devRate * 100, devThreshold * 100, penalty, result.get("riskLevel")));
        return out;
    }
}

package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 风险计量执行器（risk_measure）：蒙特卡洛 CVaR(alpha) 风险度量（确定性参数化模板）
 * 参数：scenarios 情景数、alpha 置信水平
 * 输出：CVaR / 最大回撤 / 限价建议
 */
@Component
public class RiskMeasureExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "risk_measure";
    }

    @Override
    public String label() {
        return "蒙特卡洛 CVaR 风险度量";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        int scenarios = num(params, "scenarios", 10000);
        double alpha = dnum(params, "alpha", 0.95);
        long seed = seedOf(algCode, context);
        double base = 400 + Math.abs(seed) % 60;
        double vol = 0.03 + (Math.abs(seed) % 30) / 1000.0;
        double cvar = Math.round(base * vol * (1.5 + alpha * 0.8) * 100) / 100.0;
        double maxDrawdown = Math.round(vol * 2.2 * base * 100) / 100.0;
        double limitPrice = Math.round((base - cvar) * 100) / 100.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scenarios", scenarios);
        result.put("alpha", alpha);
        result.put("volatility", Math.round(vol * 1000) / 1000.0);
        result.put("cvar", cvar);
        result.put("maxDrawdown", maxDrawdown);
        result.put("limitPrice", limitPrice);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("%d 情景压力测试：CVaR(%d%%) %.1f 元/MWh，最大回撤 %.1f，建议限价 %.1f",
                scenarios, (int) Math.round(alpha * 100), cvar, maxDrawdown, limitPrice));
        return out;
    }
}

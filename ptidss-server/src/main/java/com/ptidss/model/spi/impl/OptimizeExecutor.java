package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 优化求解执行器（optimize）：申报/持仓/偏差考核约束下的收益最大化（确定性参数化模板）
 * 参数：solver 求解器、gap 最优间隙
 * 输出：目标函数值、求解状态与申报调整建议
 */
@Component
public class OptimizeExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "optimize";
    }

    @Override
    public String label() {
        return "混合整数规划联合优化";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        String solver = text(params, "solver", "cbc");
        double gap = dnum(params, "gap", 0.01);
        long seed = seedOf(algCode, context);
        double revenue = 42000 + Math.abs(seed) % 8000;
        double deviation = 80 + Math.abs(seed) % 60;
        double objective = Math.round((revenue - deviation) * 100) / 100.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("solver", solver);
        result.put("gap", gap);
        result.put("objective", objective);
        result.put("revenue", Math.round(revenue * 100) / 100.0);
        result.put("deviationCost", Math.round(deviation * 100) / 100.0);
        result.put("status", "optimal");
        result.put("suggestion", Math.abs(seed) % 3 == 0 ? "提高日前申报量 5%" : "维持申报结构，关注偏差考核");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("%s 求解（gap=%.2f%%）：目标收益 %.1f 元，偏差成本 %.1f，状态 optimal",
                solver, gap * 100, objective, deviation));
        return out;
    }
}

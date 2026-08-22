package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 规则引擎执行器（rule_engine）：合规校验规则实时仲裁（确定性参数化模板）
 * 参数：engine 规则引擎标识
 * 输出：规则命中数与仲裁结论（最高优先）
 */
@Component
public class RuleEngineExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "rule_engine";
    }

    @Override
    public String label() {
        return "合规规则引擎仲裁";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        String engine = text(params, "engine", "drools");
        long seed = seedOf(algCode, context);
        int hit = (int) (Math.abs(seed) % 4);          // 命中 0~3 条
        boolean pass = hit <= 1;                        // ≥2 条命中即阻断
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("engine", engine);
        result.put("rulesEvaluated", 12);
        result.put("rulesHit", hit);
        result.put("passed", pass);
        result.put("verdict", pass ? "通过（最高优先规则未触发）" : "阻断（触发 " + hit + " 条约束规则，须人工复核）");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("%s 规则实时校验：评估 12 条，命中 %d 条，仲裁结论——%s",
                engine, hit, result.get("verdict")));
        return out;
    }
}

package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 报价策略执行器（quote_strategy）：成本曲线分段聚合报价（确定性参数化模板）
 * 参数：segments 分段数、uplift 上浮比例
 * 输出：分段量价表与基准报价
 */
@Component
public class QuoteStrategyExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "quote_strategy";
    }

    @Override
    public String label() {
        return "分段聚合报价策略";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        int segments = num(params, "segments", 8);
        double uplift = dnum(params, "uplift", 0.03);
        long seed = seedOf(algCode, context);
        double base = 390 + Math.abs(seed) % 60;
        List<Map<String, Object>> segList = new ArrayList<>();
        for (int i = 1; i <= segments; i++) {
            double price = Math.round((base * (1 + uplift) + i * 1.5) * 100) / 100.0;
            int qty = 50 + (int) (Math.abs(seed) % 40) + i * 5;
            Map<String, Object> seg = new LinkedHashMap<>();
            seg.put("segment", i);
            seg.put("price", price);
            seg.put("quantity", qty);
            segList.add(seg);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("segments", segments);
        result.put("uplift", uplift);
        result.put("basePrice", base);
        result.put("bidPrice", Math.round(base * (1 + uplift) * 100) / 100.0);
        result.put("segmentTable", segList);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("%d 段聚合报价：基准 %.1f 元/MWh，上浮 %.0f%%，申报价 %.1f",
                segments, base, uplift * 100, result.get("bidPrice")));
        return out;
    }
}

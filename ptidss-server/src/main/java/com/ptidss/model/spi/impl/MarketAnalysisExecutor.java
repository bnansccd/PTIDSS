package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 行情分析执行器（market_analysis）：情报舆情情感分析（确定性参数化模板）
 * 参数：window_hours 窗口、high_weight high 级情报权重
 * 输出：情感分 [-1,1]（>0 偏多）与供需修正方向
 */
@Component
public class MarketAnalysisExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "market_analysis";
    }

    @Override
    public String label() {
        return "情报舆情情感加权分析";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        int window = num(params, "window_hours", 24);
        double highWeight = dnum(params, "high_weight", 1.5);
        long seed = seedOf(algCode, context);
        double sentiment = Math.round(((seed % 1000) / 1000.0 * 2 - 1) * 100) / 100.0;
        if (seed % 3 == 0) {
            sentiment = Math.round((sentiment + highWeight * 0.2) * 100) / 100.0; // high 级情报加权
        }
        sentiment = Math.max(-1.0, Math.min(1.0, sentiment));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("windowHours", window);
        result.put("highWeight", highWeight);
        result.put("sentiment", sentiment);
        result.put("direction", sentiment > 0.15 ? "偏多" : (sentiment < -0.15 ? "偏空" : "中性"));
        result.put("adjustment", Math.round(sentiment * 0.15 * 100) / 100.0 + "%");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("近 %dh 情报情感分 %.2f（%s），供需判断修正 %.2f%%",
                window, sentiment, result.get("direction"), result.get("adjustment")));
        return out;
    }
}

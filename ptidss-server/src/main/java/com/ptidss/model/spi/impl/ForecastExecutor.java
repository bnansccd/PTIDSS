package com.ptidss.model.spi.impl;

import com.ptidss.model.spi.AlgorithmExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 预测执行器（forecast）：负荷/新能源/价格特征序列预测（确定性参数化模板）
 * 参数：horizon 预测点数、lookback 回看窗口、confidence_band 置信带（%）
 * 输出：96 点序列统计（均价/峰/谷/置信带）与置信度修正建议
 */
@Component
public class ForecastExecutor extends AbstractAlgorithmExecutor implements AlgorithmExecutor {

    @Override
    public String spiKey() {
        return "forecast";
    }

    @Override
    public String label() {
        return "特征序列预测（96 点价格）";
    }

    @Override
    public Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context) {
        int horizon = num(params, "horizon", 96);
        int lookback = num(params, "lookback", 720);
        int band = num(params, "confidence_band", 90);
        double base = 380 + Math.abs(seedOf(algCode, context)) % 90;      // 基准价 380~470
        double drift = (Math.abs(seedOf(algCode, context)) % 200 - 100) / 100.0;  // -1~1 趋势
        double peak = Math.round((base + 60 + drift * 40) * 10) / 10.0;
        double trough = Math.round((base - 50 + drift * 30) * 10) / 10.0;
        double avg = Math.round((base + drift * 15) * 10) / 10.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("horizon", horizon);
        result.put("lookback", lookback);
        result.put("avg", avg);
        result.put("peak", peak);
        result.put("trough", trough);
        result.put("confidenceBand", band + "%");
        result.put("trend", drift > 0.2 ? "上行" : (drift < -0.2 ? "下行" : "震荡"));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("spiKey", spiKey());
        out.put("label", label());
        out.put("result", result);
        out.put("summary", String.format("%d 点价格预测：均价 %.1f，峰 %.1f，谷 %.1f 元/MWh，%d%% 置信带，趋势%s",
                horizon, avg, peak, trough, band, result.get("trend")));
        return out;
    }
}

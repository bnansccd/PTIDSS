package com.ptidss.model.spi;

import com.ptidss.common.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 算法 SPI 执行器注册表（P3：算法插件化执行）
 * 构造注入全部 AlgorithmExecutor 实现并按 spiKey 建索引（重复 spiKey 后者覆盖）；
 * 决策编排按"注册算法 spiKey 优先、类目兜底"匹配执行器，未匹配返回 null（仅标注不执行）。
 * listSpis 供配置页下拉选择（"按类目默认"= 空）。
 */
@Slf4j
@Component
public class AlgorithmSpiRegistry {

    private final Map<String, AlgorithmExecutor> executors = new LinkedHashMap<>();
    /** 类目 → 默认执行器映射（内置 8 类目一一对应） */
    private final Map<String, String> categoryDefault = new LinkedHashMap<>();

    public AlgorithmSpiRegistry(List<AlgorithmExecutor> builtin) {
        for (AlgorithmExecutor ex : builtin) {
            if (StrUtils.isBlank(ex.spiKey())) {
                continue;
            }
            executors.put(ex.spiKey(), ex);
            categoryDefault.put(ex.spiKey(), ex.spiKey());
            log.info("算法 SPI 执行器已注册：{}（{}）", ex.spiKey(), ex.label());
        }
    }

    /** 执行器清单（配置页下拉：spiKey/label/category；category=匹配的算法类目） */
    public List<Map<String, Object>> listSpis() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (AlgorithmExecutor ex : executors.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("spiKey", ex.spiKey());
            item.put("label", ex.label());
            item.put("category", categoryDefault.getOrDefault(ex.spiKey(), ex.spiKey()));
            result.add(item);
        }
        return result;
    }

    /** 是否存在指定执行器（注册算法时校验 spiKey 合法性） */
    public boolean exists(String spiKey) {
        return StrUtils.isNotBlank(spiKey) && executors.containsKey(spiKey);
    }

    /**
     * 执行算法：注册算法的 spiKey 优先，否则按类目默认执行器；未匹配返回 null。
     *
     * @return {spiKey, label, result, summary, latencyMs} 或 null
     */
    public Map<String, Object> execute(String category, String spiKey, String algCode,
                                       Map<String, Object> params, Map<String, Object> context) {
        String key = StrUtils.isNotBlank(spiKey) ? spiKey : categoryDefault.get(category);
        AlgorithmExecutor ex = key == null ? null : executors.get(key);
        if (ex == null) {
            return null;
        }
        long t0 = System.currentTimeMillis();
        Map<String, Object> out = ex.execute(algCode, params, context);
        out.put("latencyMs", (int) (System.currentTimeMillis() - t0));
        return out;
    }
}

package com.ptidss.model.spi;

import java.util.Map;

/**
 * 算法 SPI 执行器接口（P3：算法插件化执行）
 * 客户注册算法时可指定 spiKey 绑定执行器（缺省按算法类目匹配默认执行器）；
 * 决策编排调用 execute 真实计算并输出 result/summary，替换算法即替换执行参数与版本。
 * 扩展方式：实现本接口并以 @Component 注册（spring.factories / 组件扫描），
 * spiKey 全局唯一，注册表按 spiKey 覆盖。
 */
public interface AlgorithmExecutor {

    /** 执行器标识（客户注册算法时可选绑定；缺省按类目匹配） */
    String spiKey();

    /** 执行器名称（配置页展示） */
    String label();

    /**
     * 执行算法计算。
     *
     * @param algCode 注册算法编码（留痕）
     * @param params  算法参数（algorithm_registry.params_schema 解析，客户可调）
     * @param context 决策上下文（input 决策输出文本 / confidence / agentCode）
     * @return {spiKey, label, result, summary, latencyMs}
     */
    Map<String, Object> execute(String algCode, Map<String, Object> params, Map<String, Object> context);
}

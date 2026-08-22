package com.ptidss.model.spi.impl;

import java.util.Map;

/**
 * 算法 SPI 执行器基类：确定性种子（algCode+input 哈希）与参数读取工具。
 * 内置执行器均为参数化模板计算——输入决策上下文 + 注册算法参数模板，
 * 输出 result（结构化结果）与 summary（留痕摘要），供决策编排标注与解释。
 */
public abstract class AbstractAlgorithmExecutor {

    protected long seedOf(String algCode, Map<String, Object> context) {
        String input = context == null ? "" : String.valueOf(context.getOrDefault("input", ""));
        return (algCode == null ? "" : algCode).hashCode() * 31L + input.hashCode();
    }

    protected int num(Map<String, Object> params, String key, int def) {
        if (params == null || params.get(key) == null) {
            return def;
        }
        try {
            return (int) Math.round(Double.parseDouble(String.valueOf(params.get(key))));
        } catch (Exception e) {
            return def;
        }
    }

    protected double dnum(Map<String, Object> params, String key, double def) {
        if (params == null || params.get(key) == null) {
            return def;
        }
        try {
            return Double.parseDouble(String.valueOf(params.get(key)));
        } catch (Exception e) {
            return def;
        }
    }

    protected String text(Map<String, Object> params, String key, String def) {
        if (params == null || params.get(key) == null) {
            return def;
        }
        return String.valueOf(params.get(key));
    }
}

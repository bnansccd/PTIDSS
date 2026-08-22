package com.ptidss.common.utils;

import java.util.UUID;

/**
 * ID 工具（雪花 ID 由应用层生成；本类提供 uuid/简单 ID 辅助）
 */
public class IdUtils {

    /** 生成 32 位无横线 UUID */
    public static String fastUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 生成带前缀的唯一键（如 trace_id） */
    public static String fastSimpleUUID(String prefix) {
        return prefix + fastUUID();
    }

    /** 生成随机 traceId */
    public static String traceId() {
        return fastUUID().substring(0, 16);
    }
}

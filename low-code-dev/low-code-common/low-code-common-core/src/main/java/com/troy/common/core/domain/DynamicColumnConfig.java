package com.troy.common.core.domain;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

/**
 * @author sym
 * @since 2025/6/20 上午10:41
 */
public class DynamicColumnConfig {

    private String field;  // 数据字段名
    private String header; // 表头标题
    private ParagraphAlignment alignment; // 对齐方式

    public DynamicColumnConfig(String field, String header) {
        this(field, header, ParagraphAlignment.CENTER);
    }

    public DynamicColumnConfig(String field, String header, ParagraphAlignment alignment) {
        this.field = field;
        this.header = header;
        this.alignment = alignment;
    }

    public String getField() {
        return field;
    }

    public String getHeader() {
        return header;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }
}

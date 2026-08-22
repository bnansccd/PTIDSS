package com.troy.form.domain.DTO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Data
public class PageColumnDTO {

    private Long id;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 列标识
     */
    private String columnMark;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列别名
     */
    private String columnAlias;

    /**
     * 合并表头名
     */
    private String mergeHeaderName;

    /**
     * 列类型 1字符串 2大文本 3二进制 4数字型 5日期
     */
    private String columnDataType;

    /**
     * 对齐方式 1 居左 2居种 3居右
     */
    private String align;

    /**
     * 最小宽度
     */
    private Integer minWidth;

    /**
     * 固定列 1 不固定 2固定居左 3固定居右
     */
    private String fixedColumn;

    /**
     * 是否 脱敏1无
        2邮箱地址
        3手机号码
        4身份证
        5地址
        6银行卡号

     */
    private String isDesensitization;

    /**
     * 是否导入
     */
    private String isImport;

    /**
     * 是否导出
     */
    private String isExport;

    /**
     * 导出高度
     */
    private Integer exportHigh;

    /**
     * 是否自动换行
     */
    private String isExportAutoWrap;

    /**
     * 是否隐藏
     */
    private String isExportHide;

    /**
     * 是否支持排序
     */
    private String isSort;

}

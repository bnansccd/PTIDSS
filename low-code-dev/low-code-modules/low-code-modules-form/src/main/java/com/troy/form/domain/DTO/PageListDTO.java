package com.troy.form.domain.DTO;

import com.troy.common.core.constant.Constants;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/11/10
 */
@Data
public class PageListDTO {
    /**
     * 自定义SQL
     */
    private String customSql;

    /**
     * 排序sql
     */
    private String orderSql;

    /**
     * 自定义弹窗标题
     */
    private String customTitle;

    /**
     * 是否默认查询
     */
    private String search = Constants.TRUE;

    /**
     * 是否复选框
     */
    private String checkBox;

    /**
     * 是否行号
     */
    private String lineNo;

    /**
     * 宽度
     */
    private Integer columnWidth;

    /**
     * 是否固定操作页
     */
    private String fixedOperationColumn;

    /**
     * 是否流程列
     */
    private String fixedFlowColumn;

    /**
     * 是否分页
     */
    private String page;

    /**
     * 分页数
     */
    private Integer pageNum;

    /**
     * 是否可以滚动
     */
    private String roll;

    /**
     * 是否树形
     */
    private String tree;

    /**
     * 树父节点字段
     */
    private String treeParentField;

    /**
     * 树节点字段
     */
    private String treeField;

    /**
     * 是否流程
     */
    private String flowable;

    /**
     * 流程范围
     */
    private String flowableType;

    /**
     * 是否禁用流程状态列
     */
    private String forbidFlowabeColumn;

    /**
     * 是否禁用当前步骤列
     */
    private String forbidCurrentColumn;

    /**
     * 自定义行样式
     */
    private String customStyle;

    /**
     * 自定义单元格样式
     */
    private String customCellsStyle;

    /**
     * 自定义合并函数
     */
    private String customMergeFunction;

    /**
     * 标题高度
     */
    private Integer excelTitleHigh;

    /**
     * 表头高度
     */
    private Integer excelHeadHigh;

    /**
     * 表格高度
     */
    private Integer excelCellHigh;

    /**
     * 自定义class
     */
    private String customClass;

    /**
     * 头部插槽
     */
    private String slopHead;

    /**
     * 尾部插槽
     */
    private String slopTail;

    /**
     * 页面ID
     */
    private Long pageId;
}

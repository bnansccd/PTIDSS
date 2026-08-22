package com.troy.gen.DTO;

import com.mybatisflex.codegen.config.ColumnConfig;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/12 11:11:43
 * @Description: MyColums
 * @Version: 1.0.0
 */
public class MyColumnConfig extends ColumnConfig {

    /**
     * JAVA类型
     */
    @ApiModelProperty(value = "JAVA类型")
    private String javaType;

    /**
     * JAVA字段名
     */
    @ApiModelProperty(value = "JAVA字段名")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @ApiModelProperty(value = "是否主键（1是）")
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @ApiModelProperty(value = "是否自增（1是）")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @ApiModelProperty(value = "是否必填（1是）")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @ApiModelProperty(value = "是否为插入字段（1是）")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @ApiModelProperty(value = "是否编辑字段（1是）")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @ApiModelProperty(value = "是否列表字段（1是）")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @ApiModelProperty(value = "是否查询字段（1是）")
    private String isQuery;

    /**
     * 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）
     */
    @ApiModelProperty(value = "查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围、GE大于等于、LE小于等于）")
    private String queryType;

    /**
     * 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件）
     */
    @ApiModelProperty(value = "显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件）")
    private String htmlType;

    /**
     * 字典类型
     */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaField() {
        return javaField;
    }

    public void setJavaField(String javaField) {
        this.javaField = javaField;
    }

    public String getIsPk() {
        return isPk;
    }

    public void setIsPk(String isPk) {
        this.isPk = isPk;
    }

    public String getIsIncrement() {
        return isIncrement;
    }

    public void setIsIncrement(String isIncrement) {
        this.isIncrement = isIncrement;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public String getIsInsert() {
        return isInsert;
    }

    public void setIsInsert(String isInsert) {
        this.isInsert = isInsert;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getIsList() {
        return isList;
    }

    public void setIsList(String isList) {
        this.isList = isList;
    }

    public String getIsQuery() {
        return isQuery;
    }

    public void setIsQuery(String isQuery) {
        this.isQuery = isQuery;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getHtmlType() {
        return htmlType;
    }

    public void setHtmlType(String htmlType) {
        this.htmlType = htmlType;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}

package com.troy.gen.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 代码生成业务表字段
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Getter
@Setter
@Table("t_gen_table_column")
public class GenTableColumnEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 归属表编号
     */
    @Column("table_id")
    private Long tableId;

    /**
     * 列名称
     */
    @Column("column_name")
    private String columnName;

    /**
     * 列描述
     */
    @Column("column_comment")
    private String columnComment;

    /**
     * 列类型
     */
    @Column("column_type")
    private String columnType;

    /**
     * JAVA类型
     */
    @Column("java_type")
    private String javaType;

    /**
     * JAVA字段名
     */
    @Column("java_field")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @Column("is_pk")
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @Column("is_increment")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @Column("is_required")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @Column("is_insert")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @Column("is_edit")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @Column("is_list")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @Column("is_query")
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    @Column("query_type")
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    @Column("html_type")
    private String htmlType;

    /**
     * 字典类型
     */
    @Column("dict_type")
    private String dictType;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    public boolean isPk() {
        return isPk(this.isPk);
    }

    public boolean isPk(String isPk) {
        return isPk != null && StringUtils.equals("1", isPk);
    }

    public boolean isInsert() {
        return isInsert(this.isInsert);
    }

    public boolean isInsert(String isInsert) {
        return isInsert != null && StringUtils.equals("1", isInsert);
    }

    public boolean isEdit() {
        return isInsert(this.isEdit);
    }

    public boolean isEdit(String isEdit) {
        return isEdit != null && StringUtils.equals("1", isEdit);
    }

    public boolean isList() {
        return isList(this.isList);
    }

    public boolean isList(String isList) {
        return isList != null && StringUtils.equals("1", isList);
    }

    public boolean isQuery() {
        return isQuery(this.isQuery);
    }

    public boolean isQuery(String isQuery) {
        return isQuery != null && StringUtils.equals("1", isQuery);
    }

    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField) {
        return StringUtils.equalsAnyIgnoreCase(javaField,
                // BaseEntity
                "createId", "createTime", "modifyId", "modifyTime", "remark",
                // TreeEntity
                "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isUsableColumn() {
        return isUsableColumn(javaField);
    }

    public static boolean isUsableColumn(String javaField) {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StringUtils.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
    }

    public boolean isRequired() {
        return isRequired(this.isRequired);
    }

    public boolean isRequired(String isRequired) {
        return isRequired != null && StringUtils.equals("1", isRequired);
    }
}

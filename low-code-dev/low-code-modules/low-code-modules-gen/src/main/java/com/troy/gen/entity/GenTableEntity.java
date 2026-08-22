package com.troy.gen.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.core.constant.GenConstants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 * 代码生成业务表
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Getter
@Setter
@Table("t_gen_table")
public class GenTableEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    @Column("table_name")
    private String tableName;

    /**
     * 表描述
     */
    @Column("table_comment")
    private String tableComment;

    /**
     * 关联子表的表名
     */
    @Column("sub_table_name")
    private String subTableName;

    /**
     * 子表关联的外键名
     */
    @Column("sub_table_fk_name")
    private String subTableFkName;

    /**
     * 实体类名称
     */
    @Column("class_name")
    private String className;

    /**
     * 使用的模板（crud单表操作,sub关联表操作, tree树表操作）
     */
    @Column("tpl_category")
    private String tplCategory;

    /**
     * 生成包路径
     */
    @Column("package_name")
    private String packageName;

    /**
     * 生成模块名
     */
    @Column("module_name")
    private String moduleName;

    /**
     * 生成业务名
     */
    @Column("business_name")
    private String businessName;

    /**
     * 生成功能名
     */
    @Column("function_name")
    private String functionName;

    /**
     * 生成功能作者
     */
    @Column("function_author")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @Column("gen_type")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @Column("gen_path")
    private String genPath;

    /**
     * 其它生成选项
     */
    @Column("options")
    private String options;

    /**
     * 备注
     */
    @Column("remarks")
    private String remarks;

    public boolean isSub() {
        return isSub(this.tplCategory);
    }

    public static boolean isSub(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_SUB, tplCategory);
    }

    public boolean isTree() {
        return isTree(this.tplCategory);
    }

    public static boolean isTree(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
    }

    public boolean isCrud() {
        return isCrud(this.tplCategory);
    }

    public static boolean isCrud(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    public boolean isSuperColumn(String javaField) {
        return isSuperColumn(this.tplCategory, javaField);
    }

    public static boolean isSuperColumn(String tplCategory, String javaField) {
        if (isTree(tplCategory)) {
            return StringUtils.equalsAnyIgnoreCase(javaField,
                    ArrayUtils.addAll(GenConstants.BASE_ENTITY));
        }
        return StringUtils.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY);
    }
}

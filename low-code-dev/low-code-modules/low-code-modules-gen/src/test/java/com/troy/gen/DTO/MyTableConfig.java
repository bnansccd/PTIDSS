package com.troy.gen.DTO;

import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.codegen.entity.Table;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/12 11:11:28
 * @Description: MyTable
 * @Version: 1.0.0
 */
public class MyTableConfig extends TableConfig {

    @ApiModelProperty(value = "关联父表的表名")
    private String subTableName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkClassName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkName;

    @ApiModelProperty(value = "实体类名称(首字母大写)")
    private String className;

    @ApiModelProperty(value = "使用的模板（crud单表操作 tree树表操作 sub主子表操作）")
    private String tplCategory;

    @ApiModelProperty(value = "树编码字段")
    private String treeCode;

    @ApiModelProperty(value = "树父编码字段")
    private String treeParentCode;

    @ApiModelProperty(value = "树名称字段")
    private String treeName;

    @ApiModelProperty(value = "主表信息")
    private Table subTable;

    public String getSubTableName() {
        return subTableName;
    }

    public void setSubTableName(String subTableName) {
        this.subTableName = subTableName;
    }

    public String getSubTableFkClassName() {
        return subTableFkClassName;
    }

    public void setSubTableFkClassName(String subTableFkClassName) {
        this.subTableFkClassName = subTableFkClassName;
    }

    public String getSubTableFkName() {
        return subTableFkName;
    }

    public void setSubTableFkName(String subTableFkName) {
        this.subTableFkName = subTableFkName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTplCategory() {
        return tplCategory;
    }

    public void setTplCategory(String tplCategory) {
        this.tplCategory = tplCategory;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getTreeParentCode() {
        return treeParentCode;
    }

    public void setTreeParentCode(String treeParentCode) {
        this.treeParentCode = treeParentCode;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public Table getSubTable() {
        return subTable;
    }

    public void setSubTable(Table subTable) {
        this.subTable = subTable;
    }
}

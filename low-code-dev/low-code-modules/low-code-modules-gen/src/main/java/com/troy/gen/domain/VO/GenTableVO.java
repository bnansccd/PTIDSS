package com.troy.gen.domain.VO;

import com.troy.common.core.constant.GenConstants;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:51
 * @Description: GenTableVO
 * @Version: 1.0.0
 */
@ApiModel(description = "代码生成业务表VO")
public class GenTableVO extends BaseVO {

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "表描述不能为空")
    private String tableComment;

    @ApiModelProperty(value = "关联父表的表名")
    private String subTableName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkClassName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkclassName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkName;

    @ApiModelProperty(value = "实体类名称(首字母大写)")
    private String className;

    @ApiModelProperty(value = "使用的模板（crud单表操作 tree树表操作 sub主子表操作）")
    private String tplCategory;

    @ApiModelProperty(value = "生成包路径")
    private String packageName;

    @ApiModelProperty(value = "")
    @NotBlank(message = "生成模块名不能为空")
    private String moduleName;

    @ApiModelProperty(value = "生成业务名")
    private String businessName;

    @ApiModelProperty(value = "生成功能名")
    private String functionName;

    @ApiModelProperty(value = "生成作者")
    private String functionAuthor;

    @ApiModelProperty(value = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    @ApiModelProperty(value = "生成路径（不填默认项目路径）")
    private String genPath;

    @ApiModelProperty(value = "主键信息")
    private GenTableColumnVO pkColumn;

    @ApiModelProperty(value = "子表信息")
    private GenTableVO subTable;

    @ApiModelProperty(value = "表列信息")
    private List<GenTableColumnVO> columns;

    @ApiModelProperty(value = "其它生成选项")
    private String options;

    @ApiModelProperty(value = "树编码字段")
    private String treeCode;

    @ApiModelProperty(value = "树父编码字段")
    private String treeParentCode;

    @ApiModelProperty(value = "树名称字段")
    private String treeName;

    @ApiModelProperty(value = "上级菜单ID字段")
    private String parentMenuId;

    @ApiModelProperty(value = "上级菜单名称字段")
    private String parentMenuName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getSubTableName() {
        return subTableName;
    }

    public void setSubTableName(String subTableName) {
        this.subTableName = subTableName;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionAuthor() {
        return functionAuthor;
    }

    public void setFunctionAuthor(String functionAuthor) {
        this.functionAuthor = functionAuthor;
    }

    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
    }

    public String getGenPath() {
        return genPath;
    }

    public void setGenPath(String genPath) {
        this.genPath = genPath;
    }

    public GenTableColumnVO getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(GenTableColumnVO pkColumn) {
        this.pkColumn = pkColumn;
    }

    public GenTableVO getSubTable() {
        return subTable;
    }

    public void setSubTable(GenTableVO subTable) {
        this.subTable = subTable;
    }

    public List<GenTableColumnVO> getColumns() {
        return columns;
    }

    public void setColumns(List<GenTableColumnVO> columns) {
        this.columns = columns;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
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

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public String getParentMenuName() {
        return parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }

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

    public static boolean isMain(GenTableVO mainTable) {
        return StringUtils.isNotNull(mainTable);
    }

    public String getSubTableFkClassName() {
        return StringUtils.convertToCamelCase(this.getSubTableFkName());
    }

    public String getSubTableFkclassName() {
        return StringUtils.uncapitalize(this.getSubTableFkClassName());
    }
}

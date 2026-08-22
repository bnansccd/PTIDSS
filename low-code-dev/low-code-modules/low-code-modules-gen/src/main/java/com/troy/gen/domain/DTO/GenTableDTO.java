package com.troy.gen.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/17 10:10:25
 * @Description: GenTableDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(value = "操作代码生成")
public class GenTableDTO implements Serializable {

    @ApiModelProperty(value = "表名称",required = true)
    @NotBlank(message = "表名称不能为空")
    @Length(max =200 ,message = "表名称最大长度不超过200")
    private String tableName;

    @ApiModelProperty(value = "表描述",required = true)
    @NotBlank(message = "表描述不能为空")
    @Length(max =500 ,message = "表描述最大长度不超过500")
    private String tableComment;

    @ApiModelProperty(value = "实体类名称",required = true)
    @NotBlank(message = "实体类名称不能为空")
    @Length(max =100 ,message = "实体类名称最大长度不超过100")
    private String className;

    @ApiModelProperty(value = "作者",required = true)
    @NotBlank(message = "作者不能为空")
    @Length(max =50 ,message = "作者最大长度不超过50")
    private String functionAuthor;

    @ApiModelProperty(value = "备注")
    @Length(max =1000 ,message = "备注最大长度不超过1000")
    private String remarks;

    @ApiModelProperty(value = "生成模板（crud 单表（增删改查）,tree 树表（增删改查）,sub 主子表（增删改查））",required = true,allowableValues = "crud,tree,sub")
    @NotBlank(message = "生成模板不能为空")
    private String tplCategory;

    @ApiModelProperty(value = "生成包路径",required = true)
    @NotBlank(message = "生成包路径不能为空")
    @Length(max =100 ,message = "生成包路径最大长度不超过100")
    private String packageName;

    @ApiModelProperty(value = "生成模块名",required = true)
    @NotBlank(message = "生成模块名不能为空")
    @Length(max =30 ,message = "生成模块名最大长度不超过30")
    private String moduleName;

    @ApiModelProperty(value = "生成业务名",required = true)
    @NotBlank(message = "生成业务名不能为空")
    @Length(max =30 ,message = "生成业务名最大长度不超过30")
    private String businessName;

    @ApiModelProperty(value = "生成功能名 ",required = true)
    @NotBlank(message = "生成功能名不能为空")
    @Length(max = 50,message = "生成功能名最大长度不超过50")
    private String functionName;

    @ApiModelProperty(value = "上级菜单Id")
    private Long parentMenuId;

    @ApiModelProperty(value = "上级菜单名称")
    @Length(max = 32,message = "上级菜单名称最大长度不超过32")
    private String parentMenuName;

    @ApiModelProperty(value = "生成代码方式（0 zip压缩包，1 自定义路径）",required = true,allowableValues = "0,1")
    @NotBlank(message = "生成代码方式不能为空")
    private String genType;

    @ApiModelProperty(value = "自定义路径")
    @Length(max =200 ,message = "自定义路径最大长度不超过200")
    private String genPath;

    @ApiModelProperty(value = "树编码字段")
    private String treeCode;

    @ApiModelProperty(value = "树父编码字段")
    private String treeParentCode;

    @ApiModelProperty(value = "树名称字段")
    private String treeName;

    @ApiModelProperty(value = "关联父表的表名")
    @Length(max =64 ,message = "关联父表的表名最大长度不超过64")
    private String subTableName;

    @ApiModelProperty(value = "本表关联父表的外键名")
    @Length(max =64 ,message = "本表关联父表的外键名最大长度不超过64")
    private String subTableFkName;

    @ApiModelProperty(value = "字段属性")
    @Valid
    @NotEmpty(message = "字段属性不能为空")
    private List<GenTableColumnsDTO> genTableColumnsDTOS=new ArrayList<>();

    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params=new HashMap<>();
}

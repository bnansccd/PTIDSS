package com.troy.gen.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/17 11:11:57
 * @Description: GenTableColumnsDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "表对应的字段实体")
public class GenTableColumnsDTO implements Serializable {

    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty(value = "字段描述", required = true)
    @NotBlank(message = "字段描述不能为空")
    @Length(max = 500, message = "字段描述最大长度不超过500")
    private String columnComment;

    @ApiModelProperty(value = "java类型", required = true, allowableValues = "Long,String,Integer,Double,BigDecimal,Date,Boolean")
    @NotBlank(message = "java类型不能为空")
    private String javaType;

    @ApiModelProperty(value = "java属性", required = true)
    @NotBlank(message = "java属性不能为空")
    @Length(max = 200, message = "java属性最大长度不超过200")
    private String javaField;

    @ApiModelProperty(value = "是否插入字段 1是", allowableValues = "0,1")
    private String isInsert;

    @ApiModelProperty(value = "是否编辑字段 1是", allowableValues = "0,1")
    private String isEdit;

    @ApiModelProperty(value = "是否列表字段 1是", allowableValues = "0,1")
    private String isList;

    @ApiModelProperty(value = "是否查询字段 1是", allowableValues = "0,1")
    private String isQuery;

    @ApiModelProperty(value = "查询方式", required = true, allowableValues = "EQ,NE,GT,GTE,LT,LTE,LIKE,BETWEEN")
    @NotBlank(message = "查询方式不能为空")
    private String queryType;

    @ApiModelProperty(value = "是否必填 1是", allowableValues = "0,1")
    private String isRequired;

    @ApiModelProperty(value = "显示类型", required = true, allowableValues = "input,textarea,select,radio,checkbox,datetime,imageUpload,fileUpload,editor")
    @NotBlank(message = "显示类型不能为空")
    private String htmlType;

    @ApiModelProperty(value = "字典类型")
    @Length(max = 200, message = "字典类型最大长度不超过200")
    private String dictType;
}

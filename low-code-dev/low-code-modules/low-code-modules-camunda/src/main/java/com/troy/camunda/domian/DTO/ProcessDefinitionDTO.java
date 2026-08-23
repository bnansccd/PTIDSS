package com.troy.camunda.domian.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/21 15:15:34
 * @Description: ProcessDefinitionDTO
 * @Version: 1.0.0
 */
@ApiModel(description = "流程定义部署")
@Data
public class ProcessDefinitionDTO implements Serializable {

    @ApiModelProperty(value = "流程定义名称（最大50）",required = true)
    @NotBlank(message = "请输入流程定议名称")
    @Length(max = 50,message = "流程定义名称字数不得超过50个字符")
    private String name;

    @ApiModelProperty(value = "流程定义资源文件路径",required = true)
    @NotBlank(message = "流程定义资源文件路径")
    @Length(max = 100,message = "流程定义资源文件路径字数不得超过100个字符")
    private String resourcePath;
}

package com.troy.camunda.domian.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/21 14:14:42
 * @Description: 流程定义分页查询实体
 * @Version: 1.0.0
 */
@ApiModel(description = "流程定义分页查询实体")
@Data
public class ProcessDefinitionSearchDTO extends PageDTO {

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;
}

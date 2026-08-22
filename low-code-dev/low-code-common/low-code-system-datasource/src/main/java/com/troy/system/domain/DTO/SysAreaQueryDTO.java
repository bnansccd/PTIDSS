package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/17 10:10:16
 * @Description: SysAreaQueryDTO
 * @Version: 1.0.0
 */
@ApiModel(description = "区域查询字段")
@Data
public class SysAreaQueryDTO implements Serializable {

    @ApiModelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "区域名称")
    private String name;

    @ApiModelProperty(value = "行政编码")
    private String adcode;
}

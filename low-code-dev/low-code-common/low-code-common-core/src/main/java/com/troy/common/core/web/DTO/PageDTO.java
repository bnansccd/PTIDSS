package com.troy.common.core.web.DTO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/4 14:14:42
 * @Description: PageDto
 * @Version: 1.0.0
 */
@ApiModel(description = "分页参数")
@Data
public class PageDTO implements Serializable {

    @ApiModelProperty(value = "当前页")
    @Min( value = 1L, message = "当前分页必须大于0")
    private Long current = 1L;

    @Min( value = 1L, message = "当前分页显示条数必须大于0")
    private Long size = 5L;

    @ApiModelProperty(value = "排序字段")
    private List<OrderByDTO> orderByDTOS = new ArrayList<>();
}

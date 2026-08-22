package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/14 13:13:16
 * @Description: SysAppQueryDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "app应用查询参数")
public class SysAppQueryDTO extends PageDTO {

    @ApiModelProperty(value = "应用名称")
    private String name;
}

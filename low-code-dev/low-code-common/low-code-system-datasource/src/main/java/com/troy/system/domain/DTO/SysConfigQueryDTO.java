package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname: QuerySysConfigDto
 * @Description:
 * @Date 2022/9/12
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "参数列表查询实体")
public class SysConfigQueryDTO extends PageDTO {

    @ApiModelProperty(value = "参数名称")
    private String configName;

}

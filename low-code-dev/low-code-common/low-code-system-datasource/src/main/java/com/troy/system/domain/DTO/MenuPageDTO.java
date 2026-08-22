package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxl
 * @Date 2023/3/30
 */
@Data
public class MenuPageDTO extends PageDTO {

    @ApiModelProperty("菜单名称")
    private String dictName;

    @ApiModelProperty("菜单父级Id")
    private Long parentId;

}

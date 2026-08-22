package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/10 15:15:55
 * @Description: SysUserSeachDTO
 * @Version: 1.0.0
 */

@Data
@ApiModel(description = "用户列表分页查询参数")
public class SysUserSearchDTO extends PageDTO {

    @ApiModelProperty(value = "用户名")
    private String username;
}

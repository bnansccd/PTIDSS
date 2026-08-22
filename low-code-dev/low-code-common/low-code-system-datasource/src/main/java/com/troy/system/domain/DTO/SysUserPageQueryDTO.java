package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Classname: SysUserQueryDTO
 * @Description:
 * @Date 2022/9/2
 * @Author: yzy
 * @Version
 **/

@Data
@ApiModel(description = "查询用户列表")
public class SysUserPageQueryDTO extends PageDTO {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "部门id")
    private Long departId;
}

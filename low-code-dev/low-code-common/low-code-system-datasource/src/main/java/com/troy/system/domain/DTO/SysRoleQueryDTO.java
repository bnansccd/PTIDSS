package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname: QuerySysRoleListDTO
 * @Description:
 * @Date 2022/9/7
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "角色分頁列表")
public class SysRoleQueryDTO extends PageDTO {

    @ApiModelProperty(value = "角色名字")
    private String roleName;
}

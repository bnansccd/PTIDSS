package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname: QuerySysMenuDTO
 * @Description:
 * @Date 2022/9/8
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "菜单查询实体")
public class SysMenuQueryDTO implements Serializable {

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    private Long appId;
}

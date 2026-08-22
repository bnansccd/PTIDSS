package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.NoPageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname: QuerySysDepartListDTO
 * @Description:
 * @Date 2022/9/6
 * @author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "查询部门")
public class SysDepartQueryDTO extends NoPageDTO implements Serializable {

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "上级部门")
    private Long parentId;

    private String enable;

}

package com.troy.system.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname: QueryDictDTO
 * @Description:
 * @Date 2022/9/11
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "字典查询实体")
public class SysDictQueryDTO implements Serializable {

    @ApiModelProperty(value = "字典名称")
    private String dictName;

}

package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname: QuerySysPostListDto
 * @Description:
 * @Date 2022/9/6
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "查询岗位列表")
public class SysPostQueryDTO extends PageDTO {

    @ApiModelProperty(value = "岗位名字")
    private  String postName;

    @ApiModelProperty(value = "岗位编码")
    private String postCode;

    private String sfqy;


}

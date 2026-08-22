package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.NodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname: SysDictVO
 * @Description:
 * @Date 2022/9/11
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "字典实体回显")
public class SysDictVO extends NodeVO {


    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "字典父类型")
    private String parentType;

    @ApiModelProperty(value = "备注")
    private String remarks;

}

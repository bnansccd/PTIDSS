package com.troy.common.core.web.VO;


import com.troy.common.core.anotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Classname: NodeVO
 * @Description:
 * @Date 2022/9/11
 * @Author: yzy
 * @Version
 **/
@Data
public class NodeVO<T> extends BaseVO {

    @ApiModelProperty(value = "父id")
    @Excel(name = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    private Integer sort;

    @ApiModelProperty(value = "祖级列表")
    private String ancestors;

    @ApiModelProperty(value = "子集")
    private Collection<T> children = new ArrayList<>();

}

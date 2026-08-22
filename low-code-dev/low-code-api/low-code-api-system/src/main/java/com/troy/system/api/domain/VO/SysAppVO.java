package com.troy.system.api.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chenxl
 * @Date 2023/3/15
 */
@Data
@ApiModel(description = "公司应用页")
public class SysAppVO {


    @ApiModelProperty(value = "应用ID")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标签")
    private String icon;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "启用状态 0未启用 1启用")
    private String status;


    @ApiModelProperty(value = "地址")
    private String url;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "应用编码")
    private String code;

    @ApiModelProperty(value = "背景图", required = true)
    private String background;

    private Integer sort;

    @ApiModelProperty(value = "1 内部 2外部", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String type;

    private List<SysMenuVO> sysMenuVOS;

}

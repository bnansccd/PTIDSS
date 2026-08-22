package com.troy.system.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author chenxl
 * @Date 2023/3/15
 */
@Data
@ApiModel(description = "公司应用页")
public class TenantAppVO {


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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validEndTime;

    private Long appId;

}

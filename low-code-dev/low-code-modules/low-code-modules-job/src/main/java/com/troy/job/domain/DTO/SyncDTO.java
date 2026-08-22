package com.troy.job.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SyncDTO {

    @ApiModelProperty("数据源来源标识")
    private String fromTarget;

    @ApiModelProperty("数据源保存标识")
    private String toTarget;

    private Long fromTenantId;

    private Long tenantId;

    @ApiModelProperty("表来源标识")
    private String fromTable;

    /**
     * 脚本
     */
    private String script;

    @ApiModelProperty("表保存标识")
    private String toTable;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /**
     * 使用默认值字段
     */
    @ApiModelProperty("要使用默认值的字段")
    private List<String> defaultAggregationParam;

    /**
     * 主键
     */
    @ApiModelProperty("多维度主键 暂时无用")
    private List<String> fromKeys;

    /**
     * 1全量 2增量
     */
    @ApiModelProperty("类型  1全量 2增量")
    private String type;

}

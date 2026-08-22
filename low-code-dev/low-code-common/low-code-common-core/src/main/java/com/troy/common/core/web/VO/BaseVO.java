package com.troy.common.core.web.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 16:16:53
 * @Description: VO基类
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "VO基类")
public class BaseVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "创建人id")
    private Long createId;

    @ApiModelProperty(value = "创建人")
    private String createName;

    @ApiModelProperty(value = "创建部门id")
    private Long createDepartId;

    @ApiModelProperty(value = "创建部门")
    private String createDepartName;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改人id")
    private Long modifyId;

    @ApiModelProperty(value = "修改人")
    private String modifyName;

    @ApiModelProperty(value = "修改部门id")
    private Long modifyDepartId;

    @ApiModelProperty(value = "修改部门")
    private String modifyDepartName;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

}

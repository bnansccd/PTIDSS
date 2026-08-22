package com.troy.gen.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/16 14:14:35
 * @Description: DbTableDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "数据库表查询")
public class DbTableDTO extends PageDTO {

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "表备注")
    private String tableComment;

    @ApiModelProperty(value = "创建开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    @ApiModelProperty(value = "创建结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}

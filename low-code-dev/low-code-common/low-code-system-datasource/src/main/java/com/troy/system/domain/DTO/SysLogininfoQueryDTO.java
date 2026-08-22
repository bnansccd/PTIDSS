package com.troy.system.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Classname: QuerySysLogininfoDTO
 * @Description:
 * @Date 2022/9/19
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "查询系统访问记录")
public class SysLogininfoQueryDTO extends PageDTO {

    @ApiModelProperty(value = "用户账户")
    private String userName;

    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}

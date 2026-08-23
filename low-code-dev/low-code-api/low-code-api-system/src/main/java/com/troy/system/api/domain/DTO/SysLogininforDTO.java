package com.troy.system.api.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 15:15:59
 * @Description: 系统访问记录
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "系统访问记录")
public class SysLogininforDTO implements Serializable {

    @ApiModelProperty(value = "用户账号", required = true)
    @NotBlank(message = "帐号不能为空")
    @Length(max = 32, message = "帐号最大长度是32")
    private String username;

    @ApiModelProperty(value = "状态 0成功 1失败")
    private String status;

    @ApiModelProperty(value = "地址")
    private String loginIp;

    @ApiModelProperty(value = "描述")
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "访问时间")
    private Date accessTime;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;
}

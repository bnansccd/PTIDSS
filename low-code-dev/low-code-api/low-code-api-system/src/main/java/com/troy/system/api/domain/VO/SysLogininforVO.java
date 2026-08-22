package com.troy.system.api.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname: SysLoginInforVO
 * @Description:
 * @Date 2022/9/19
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "系统访问记录")
public class SysLogininforVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "登录IP地址")
    private String loginIp;

    @ApiModelProperty(value = "登录状态（0成功1失败）")
    private String status;

    @ApiModelProperty(value = "提示信息")
    private String msg;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "访问时间")
    private Date accessTime;
}

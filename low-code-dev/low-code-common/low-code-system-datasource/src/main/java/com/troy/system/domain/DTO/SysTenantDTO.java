package com.troy.system.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/22 11:11:08
 * @Description: SysTenantDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "租户操作类")
public class SysTenantDTO implements Serializable {

    @ApiModelProperty(value = "租户名称（最大长度100）",required = true)
    @NotBlank(message = "请输入租户名称")
    @Length(max = 100,message = "租户名称最大长度不超过100")
    private String name;

    @ApiModelProperty(value = "租户编码（最大长度100）",required = true)
    @NotBlank(message = "请输入租户编码")
    @Length(max = 100,message = "租户编码最大长度不超过100")
    private String code;

    @ApiModelProperty(value = "租户生效时间(格式：yyyy-MM-dd HH:mm:ss)",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "请选择租户生效时间")
    private Date startTime;

    @ApiModelProperty(value = "租户失效时间(格式：yyyy-MM-dd HH:mm:ss)",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "请选择租户失效时间")
    private Date endTime;

}

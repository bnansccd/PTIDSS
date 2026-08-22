package com.troy.system.domain.DTO;

import com.troy.common.core.constant.RegexConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/22 11:11:13
 * @Description: SysTenantInsertDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "租户添加实体")
public class SysTenantInsertDTO extends SysTenantDTO{

    @ApiModelProperty(value = "用户名(最大长度不超过30)", required = true)
    @NotBlank(message = "用户名不能为空！")
    @Length(max = 30, message = "账号长度不能超过30个字符！")
    private String username;

    @ApiModelProperty(value = "手机号（正则："+ RegexConstants.PHONE_CHS+"）", required = true)
    @NotBlank(message = "请输入手机号")
    @Pattern(regexp ="^$|"+RegexConstants.PHONE_CHS, message = "请输入正确的手机号！")
    private String phone;

    @ApiModelProperty(value = "真实姓名((最大长度不超过30))",required = true)
    @NotBlank(message = "用户名不能为空！")
    @Length(max = 30, message = "用户名长度不能超过30个字符！")
    private String realName;
}

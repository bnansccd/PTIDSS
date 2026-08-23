package com.troy.system.api.domain.DTO;

import com.troy.common.core.constant.UserConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/2 17:17:32
 * @Description: RegisterDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "注册用户信息")
public class RegisterDTO implements Serializable {


    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "请输入用户名")
    @Length(min = UserConstants.USERNAME_MIN_LENGTH,max = UserConstants.USERNAME_MAX_LENGTH,message = "账户长度必须在2到20个字符之间")
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    @NotBlank(message = "请输入用户密码")
    @Length(min = UserConstants.PASSWORD_MIN_LENGTH,max = UserConstants.PASSWORD_MAX_LENGTH,message = "密码长度必须在5到20个字符之间")
    private String password;

    @ApiModelProperty(value = "租户id", required = true)
    @NotBlank(message = "请选择租户")
    private Long tenantId;
}

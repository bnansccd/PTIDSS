package com.troy.system.domain.DTO;

import com.troy.common.core.constant.RegexConstants;
import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * @Classname: SysUserDTO
 * @Description:
 * @Date 2022/9/2
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "添加/修改用户")
public class SysUserDTO implements Serializable {

    @ApiModelProperty(value = "用户名(最大长度不超过30)", required = true)
    @NotBlank(message = "用户名不能为空！")
    @Length(max = 30, message = "账号长度不能超过30个字符！")
    private String username;

    @ApiModelProperty(value = "手机号（正则："+RegexConstants.PHONE_CHS+"）", required = true)
    @NotBlank(message = "请输入手机号")
    @Pattern(regexp ="^$|"+RegexConstants.PHONE_CHS, message = "请输入正确的手机号！")
    private String phone;

    @ApiModelProperty(value = "真实姓名((最大长度不超过30))",required = true)
    @NotBlank(message = "用户名不能为空！")
    @Length(max = 30, message = "用户名长度不能超过30个字符！")
    private String realName;

    @ApiModelProperty(value = "启用停用(参考字典：STATUS_TYPE)",required = true)
    @NotBlank(message = "用户状态不能为空")
    @ValidDict(parentType = DictTypeEnums.STATUS_TYPE,message = "请选择正确的用户状态")
    private String status;

    @ApiModelProperty(value = "邮箱（正则："+RegexConstants.EMAIL_CHS+"）")
    @Pattern(regexp = RegexConstants.EMAIL_CHS, message = "请输入符合规范的邮箱格式！")
    private String email;

    @ApiModelProperty(value = "性别(参考字典：SEX)")
    @ValidDict(parentType = DictTypeEnums.SEX,message = "请选择正确的性别")
    private String sex;

    @ApiModelProperty(value = "头像地址（最大长度不超过200）")
    @Length(max = 200, message = "头像地址长度不能超过200个字符！")
    private String headUrl;

    @ApiModelProperty(value = "部门id")
    private Long departId;

    @ApiModelProperty(value = "岗位")
    private List<Long> postIds;

    @ApiModelProperty(value = "角色")
    private List<Long> roles;

}

package com.troy.system.api.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.common.core.anotation.SensitiveData;
import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 16:16:44
 * @Description: SysUserVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "用户信息")
public class SysUserVO extends BaseVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    @SensitiveData(type = SensitiveData.SensitiveType.PHONE)
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @SensitiveData(type = SensitiveData.SensitiveType.EMAIL)
    private String email;

    @ApiModelProperty(value = "性别(0女1男)")
    private String sex;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @ApiModelProperty(value = "最后登录ip")
    private String lastLoginIp;

    @ApiModelProperty(value = "登录次数")
    private Long loginTimes;

    @ApiModelProperty(value = "启用停用(0启用1停用)")
    private String status;

    @ApiModelProperty(value = "部门id")
    private Long departId;

    @ApiModelProperty(value = "头像地址")
    private Long headUrl;

    @ApiModelProperty(value = "是否是超管")
    private Boolean isAdmin;

    private String token;

    private Character first;

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long id) {
        return id != null && 1L == id;
    }
}

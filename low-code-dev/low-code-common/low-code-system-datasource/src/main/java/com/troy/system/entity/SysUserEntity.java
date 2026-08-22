package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.annotation.Encrypted;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 用户管理
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_user")
public class SysUserEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Column("username")
    private String username;

    /**
     * 密码
     */
    @Column(value = "password",comment = "密码")
    @Consistency
    @Encrypted
    private String password;

    /**
     * 手机号
     */
    @Column(value = "phone",comment = "手机号")
    @Encrypted
    @Consistency
    private String phone;

    /**
     * 邮箱
     */
    @Column("email")
    @Encrypted
    private String email;

    /**
     * 性别(0女1男)
     */
    @Column("sex")
    private String sex;

    /**
     * 真实姓名
     */
    @Column(value = "real_name",comment = "真实姓名")
    @Consistency
    private String realName;

    /**
     * 启用停用(0启用1停用)
     */
    @Column("status")
    private String status;

    /**
     * 部门id
     */
    @Column("depart_id")
    private Long departId;

    /**
     * 头像地址
     */
    @Column("head_url")
    private Long headUrl;

    /**
     * 登录IP
     */
    @Column("last_login_ip")
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @Column("login_times")
    private Long loginTimes;

    /**
     * 登录IP
     */
    @Column("last_login_time")
    private Date lastLoginTime;

    @Column("token")
    private String token;


    @Column("data_from")
    private String dataFrom;

}

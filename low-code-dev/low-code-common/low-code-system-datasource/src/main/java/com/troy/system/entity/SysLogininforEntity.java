package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 系统访问记录
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table(value = "t_sys_logininfor",comment = "系统访问记录")
public class SysLogininforEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @Column(value ="username",comment = "用户账号")
    @Consistency
    private String username;

    /**
     * 登录IP地址
     */
    @Column(value ="login_ip",comment = "登录IP地址")
    @Consistency
    private String loginIp;

    /**
     * 登录状态（0成功1失败）
     */
    @Column(value ="status",comment = "登录状态（0成功1失败）")
    @Consistency
    private String status;

    /**
     * 提示信息
     */
    @Column(value ="msg",comment = "提示信息")
    @Consistency
    private String msg;

    /**
     * 访问时间
     */
    @Column(value ="access_time",comment = "访问时间")
    private Date accessTime;

}


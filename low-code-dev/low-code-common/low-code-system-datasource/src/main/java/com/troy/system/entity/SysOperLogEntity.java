package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 操作日志记录
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_oper_log")
public class SysOperLogEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模块标题
     */
    @Column("title")
    private String title;

    /**
     * 业务类型（0其它1新增2修改3删除）
     */
    @Column("business_type")
    private Integer businessType;

    /**
     * 方法名称
     */
    @Column("method")
    private String method;

    /**
     * 请求方式
     */
    @Column("request_method")
    private String requestMethod;

    /**
     * 操作类别（0其它，1后台用户，2手机端用户）
     */
    @Column("operator_type")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @Column(value ="oper_name",comment = "操作人员")
    @Consistency
    private String operName;

    /**
     * 部门名称
     */
    @Column("dept_name")
    private String deptName;

    /**
     * 请求URL
     */
    @Column("oper_url")
    private String operUrl;

    /**
     * 主机地址
     */
    @Column("oper_ip")
    private String operIp;

    /**
     * 操作地点
     */
    @Column("oper_location")
    private String operLocation;

    /**
     * 请求参数
     */
    @Column(value ="oper_param",comment = "请求参数")
    @Consistency
    private String operateParam;

    /**
     * 返回参数
     */
    @Column(value ="json_result",comment = "返回参数")
    @Consistency
    private String jsonResult;

    /**
     * 操作状态
     */
    @Column("status")
    private Integer status;

    /**
     * 错误消息
     */
    @Column("error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Column("oper_time")
    private Date operTime;

    /**
     * 客户端系统
     */
    @Column("operator")
    private String operator;

    /**
     * 浏览器版本
     */
    @Column("browser")
    private String browser;
}

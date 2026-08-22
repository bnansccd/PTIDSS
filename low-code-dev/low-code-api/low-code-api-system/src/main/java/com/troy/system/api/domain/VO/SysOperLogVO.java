package com.troy.system.api.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname: SysOperLogVO
 * @Description:
 * @Date 2022/9/19
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "操作日志")
public class SysOperLogVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "模块标题")
    private String title;

    @ApiModelProperty(value = "业务类型（0其它1新增2修改3删除）")
    private Integer businessType;

    @ApiModelProperty(value = "业务类型（0其它1新增2修改3删除）")
    private String businessTypeName;

    @ApiModelProperty(value = "方法名称")
    private String method;

    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    @ApiModelProperty(value = "操作类别（0其它，1后台用户，2手机端用户）")
    private Integer operatorType;

    @ApiModelProperty(value = "操作类别（0其它，1后台用户，2手机端用户）")
    private String operatorTypeName;

    @ApiModelProperty(value = "操作人员")
    private String operName;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "请求URL")
    private String operUrl;

    @ApiModelProperty(value = "主机地址")
    private String operIp;

    @ApiModelProperty(value = "操作地点")
    private String operLocation;

    @ApiModelProperty(value = "请求参数")
    private String operParam;

    @ApiModelProperty(value = "返回参数")
    private String jsonResult;

    @ApiModelProperty(value = "操作状态")
    private Integer status;

    @ApiModelProperty(value = "错误消息")
    private String errorMsg;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "客户端系统")
    private String operator;

    @ApiModelProperty(value = "browser")
    private String browser;
}

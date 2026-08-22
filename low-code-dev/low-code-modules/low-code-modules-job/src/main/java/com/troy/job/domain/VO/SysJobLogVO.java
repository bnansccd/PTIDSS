package com.troy.job.domain.VO;

import com.troy.common.core.anotation.Excel;
import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 14:14:56
 * @Description: SysJobLogVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(value = "定时任务日志")
public class SysJobLogVO extends BaseVO {

    @Excel(name = "任务名称")
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @Excel(name = "任务组名")
    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @Excel(name = "调用目标字符串")
    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;

    @Excel(name = "日志信息")
    @ApiModelProperty(value = "日志信息")
    private String jobMessage;

    @Excel(name = "执行状态" , readConverterExp = "0=正常,1=失败")
    @ApiModelProperty(value = "执行状态（0正常 1失败）")
    private String status;

    @Excel(name = "异常信息")
    @ApiModelProperty(value = "异常信息")
    private String exceptionInfo;
}

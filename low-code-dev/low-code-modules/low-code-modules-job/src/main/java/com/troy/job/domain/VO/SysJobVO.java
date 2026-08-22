package com.troy.job.domain.VO;

import com.troy.common.core.anotation.Excel;
import com.troy.common.core.constant.ScheduleConstants;
import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 13:13:11
 * @Description: SysJobVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "定时任务调度表返回实体")
public class SysJobVO extends BaseVO {

    @Excel(name = "任务名称")
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @Excel(name = "任务组名")
    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @Excel(name = "调用目标字符串")
    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;

    @Excel(name = "执行表达式 ")
    @ApiModelProperty(value = " cron执行表达式")
    private String cronExpression;

    @Excel(name = "计划策略 " , readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    @ApiModelProperty(value = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    @Excel(name = "并发执行" , readConverterExp = "0=允许,1=禁止")
    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    @Excel(name = "任务状态" , readConverterExp = "0=正常,1=暂停")
    @ApiModelProperty(value = "任务状态（0正常 1暂停）")
    private String status;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;
}

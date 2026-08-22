package com.troy.job.domain.DTO;

import com.troy.common.core.constant.ScheduleConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 13:13:41
 * @Description: SysJobDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "定时任务操作类")
public class SysJobDTO implements Serializable {

    @ApiModelProperty(value = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    @ApiModelProperty(value = "任务组名")
    @NotBlank(message = "任务组名不能为空")
    @Size(min = 0, max = 64, message = "任务组名不能超过64个字符")
    private String jobGroup;

    @ApiModelProperty(value = "调用目标字符串")
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(min = 0, max = 500, message = "调用目标字符串长度不能超过500个字符")
    private String invokeTarget;

    @ApiModelProperty(value = "cron执行表达式")
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    private String cronExpression;

    @ApiModelProperty(value = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    @ApiModelProperty(value = "任务状态（0正常 1暂停）")
    private String status;

    @ApiModelProperty(value = "备注")
    @Size(min = 0, max = 500, message = "备注不能超过64个字符")
    private String remark;
}

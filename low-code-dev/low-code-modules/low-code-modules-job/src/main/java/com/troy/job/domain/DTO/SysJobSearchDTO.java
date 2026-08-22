package com.troy.job.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 11:11:51
 * @Description: SysJobDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "定时任务调度表分页查询参数")
public class SysJobSearchDTO extends PageDTO {

    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @ApiModelProperty(value = "任务状态（0正常 1暂停）")
    private String status;

    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;
}

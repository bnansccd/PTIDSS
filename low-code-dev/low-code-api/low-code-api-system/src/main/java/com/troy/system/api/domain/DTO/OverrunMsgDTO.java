package com.troy.system.api.domain.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sym
 * @since 2025/7/28 上午10:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverrunMsgDTO {
    @ApiModelProperty("通过用户 id 查询手机号发短信")
    private Long userId;

    @ApiModelProperty("和用户 id 二选一填入")
    private String phone;

    @ApiModelProperty("短信展示部门")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;


    @ApiModelProperty("一超四罚案件号")
    private String caseNumber;

    @ApiModelProperty("距离办结天数")
    private Integer dayCount;

    @ApiModelProperty("用于区分预警、超期、抄告短信，枚举OVERRUN_MSG_TYPE")
    private String msgMark;

}

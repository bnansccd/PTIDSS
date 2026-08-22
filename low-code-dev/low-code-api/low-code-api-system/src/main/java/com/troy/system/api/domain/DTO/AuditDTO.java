package com.troy.system.api.domain.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/18 13:13:50
 * @Description: AuditDTO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "审计查询")
public class AuditDTO implements Serializable {

    @ApiModelProperty(value = "用户Id")
    private List<Long> userIds=new ArrayList<>();

    @ApiModelProperty(value = "部门id")
    private List<Long> departIds=new ArrayList<>();
}

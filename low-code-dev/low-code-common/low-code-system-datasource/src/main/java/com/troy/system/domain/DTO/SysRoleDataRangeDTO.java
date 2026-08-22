package com.troy.system.domain.DTO;

import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname: SysRoleDataRange
 * @Description:
 * @Date 2022/9/7
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel
@Valid
public class SysRoleDataRangeDTO implements Serializable {

    @ApiModelProperty(value = "数据范围（参考字典：DATA_RANGE）")
    @NotBlank(message = "请选择数据权限")
    @ValidDict(parentType = DictTypeEnums.DATA_RANGE, message = "请选择正确的数据权限")
    private String dataRange;

    @ApiModelProperty(value = "部门id")
    private List<Long> departIds = new ArrayList<>();
}

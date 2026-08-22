package com.troy.system.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/8 15:15:56
 * @Description: DataPermissionsVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "数据权限")
public class DataPermissionsVO implements Serializable {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "部门id")
    private List<Long> departIds=new ArrayList<>();

    @ApiModelProperty(value = "接近的sql")
    private String sqlStr;
}

package com.troy.system.api.domain.VO;

import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/9 13:13:23
 * @Description: SysPostVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "岗位信息")
public class SysPostVO extends BaseVO {

    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ApiModelProperty(value = "岗位code")
    private String postCode;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "岗位描述")
    private String remarks;

    @ApiModelProperty(value = "对应用的用户id")
    private List<Long> userIds = new ArrayList<>();

    private String sfqy;
}

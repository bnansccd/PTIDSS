package com.troy.system.domain.VO;

import com.troy.common.core.web.VO.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/17 10:10:18
 * @Description: SysAreaVO
 * @Version: 1.0.0
 */
@ApiModel(description = "区域信息")
@Data
public class SysAreaVO extends BaseVO {

    @ApiModelProperty(value = "城市编码")
    private String citycode;

    @ApiModelProperty(value = "区域编码(街道没有独有的adcode，均继承父类（区县）的adcode)")
    private String adcode;

    @ApiModelProperty(value = "行政区名称")
    private String name;

    @ApiModelProperty(value = "行政区边界坐标点(当一个行政区范围，由完全分隔两块或者多块的地块组成，每块地的 polyline 坐标串以 | 分隔 。如北京 的 朝阳区)")
    private String polyline;

    @ApiModelProperty(value = "经度")
    private String lon;

    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "country:国家province:省份（直辖市会在province显示）city:市（直辖市会在province显示）district:区县street:街道")
    private String level;

    @ApiModelProperty(value = "父级编码")
    private String parentCode;

    @ApiModelProperty(value = "父级id")
    private Long parentId;

    @ApiModelProperty(value = "祖级编码，逗号隔开")
    private String ancestorsCode;

    @ApiModelProperty(value = "祖级id，逗号隔开")
    private String ancestors;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "子集")
    private List<SysAreaVO> sysAreaVOS=new ArrayList<>();
}

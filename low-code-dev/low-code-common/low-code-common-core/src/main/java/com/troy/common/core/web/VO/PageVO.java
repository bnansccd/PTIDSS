package com.troy.common.core.web.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/4 14:14:58
 * @Description: PageVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "分布返回")
public class PageVO<T> implements Serializable {

    @ApiModelProperty(value = "数据")
    private List<T> records = new ArrayList<>();

    @ApiModelProperty(value = "总条数")
    private Long total = 0L;

    @ApiModelProperty(value = "当前页")
    private Long current = 1L;

    @ApiModelProperty(value = "页大小")
    private Long size = 5L;

    @ApiModelProperty(value = "总页数")
    private Long pages = 0L;
}

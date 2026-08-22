package com.troy.common.core.web.DTO;

import com.troy.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/8 10:10:39
 * @Description: OrderByDTO
 * @Version: 1.0.0
 */
@ApiModel(description = "排序规则")
public class OrderByDTO implements Serializable {

    @ApiModelProperty(value = "排序字段")
    private String column = "create_time";

    @ApiModelProperty(value = "排序规则,true升序false降序", allowableValues = "true,false")
    private Boolean isAsc = Boolean.FALSE;

    public String getColumn() {
        if (StringUtils.isBlank(column)) {
            column = "create_time";
        }
        return column;
    }

    public void setColumn(String column) {
        if (StringUtils.isNotBlank(column)) {
            if (!StringUtils.endsWith(column, "_")) {
                column = StringUtils.toUnderScoreCase(column);
            }
        }
        this.column = column;
    }

    public boolean isAsc() {
        if (StringUtils.isNull(isAsc)) {
            isAsc = Boolean.TRUE;
        }
        return isAsc;
    }

    public void setAsc(boolean asc) {
        isAsc = asc;
    }
}

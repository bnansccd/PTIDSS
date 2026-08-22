package com.troy.gen.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/16 16:16:34
 * @Description: DbTableColumnVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "数据库表对应的字段")
public class DbTableColumnVO implements Serializable {

    @ApiModelProperty(value = "列名称")
    private String columnName;

    @ApiModelProperty(value = "是否必填（1是）")
    private String isRequired;

    @ApiModelProperty(value = "是否主键（1是）")
    private String isPk;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "列描述")
    private String columnComment;

    @ApiModelProperty(value = "是否自增（1是）")
    private String isIncrement;

    @ApiModelProperty(value = "列类型")
    private String columnType;


}

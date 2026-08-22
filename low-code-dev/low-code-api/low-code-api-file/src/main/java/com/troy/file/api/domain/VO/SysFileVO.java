package com.troy.file.api.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/29 10:10:30
 * @Description: 文件信息
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "文件信息")
public class SysFileVO implements Serializable {

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件地址")
    private String url;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "文件后缀")
    private String suffix;

    @ApiModelProperty(value = "文件路径")
    private String filePath;
}

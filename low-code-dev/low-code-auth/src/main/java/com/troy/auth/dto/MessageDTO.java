package com.troy.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author chenxl
 * @date 2024/1/11
 */
@Data
@ApiModel("消息")
public class MessageDTO {

    private String title;

    private List<String> toDepts;

    private String toTerminal;

    private List<String> toUsers;

    @ApiModelProperty("\"bodyFields\":[\n" +
            " {\n" +
            " \"name\":\"姓名\",\n" +
            " \"content\":\"张三\"\n" +
            " },\n" +
            " {\n" +
            " \"name\":\"性别\",\n" +
            " \"content\":\"男\"\n" +
            " }\n" +
            " ],")
    private List<Map<String, String>> bodyFields;


    @ApiModelProperty(":[\n" +
            " {\n" +
            " \"pcActionUrl\":\"https://www.jd.com\",\n" +
            " \"mobileActionUrl\":https://www.jd.com\n" +
            "\"openType\":\"0\"\n" +
            " }\n" +
            " ]")
    private List<Map<String, String>> buttonInfo;

}

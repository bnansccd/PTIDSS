package com.troy.form.domain.DTO;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/11/14
 */
@Data
public class FormDTO {

    private String mark;

    /**
     * 参数规定 表名-作为头；表结构-作为参数
     */
    private JSONObject formData;


    /**
     *
     * {
     *     "mark":"",
     *     "formData":{
     *         "t_rd_route":{
     *             "name":"路",
     *             "code":"code",
     *             "t_rd_route_section@One":{},
     *             "t_rd_gps@List":[]
     *         }
     *     }
     * }
     *
     *
     */
}

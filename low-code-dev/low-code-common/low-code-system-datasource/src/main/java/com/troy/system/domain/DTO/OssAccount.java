package com.troy.system.domain.DTO;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/12/4
 */
@Data
public class OssAccount {

    private String id;

    private JSONObject attributes;

}

package com.troy.system.domain.DTO;

import lombok.Data;

/**
 * @author chenxl
 * @date 2023/12/4
 */
@Data
public class OssToken {

    private String access_token;

    private String refresh_token;

}

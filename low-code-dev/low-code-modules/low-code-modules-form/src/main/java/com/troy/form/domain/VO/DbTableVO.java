package com.troy.form.domain.VO;

import lombok.Data;

/**
 * @author chenxl
 * @date 2023/10/18
 */
@Data
public class DbTableVO {

    private String tableName;

    private String engine;

    private String tableComment;
}

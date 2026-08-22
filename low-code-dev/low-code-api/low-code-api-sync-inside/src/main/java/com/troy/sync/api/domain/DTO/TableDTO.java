package com.troy.sync.api.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @Author: chenxl
 * @Date: 2023-05-17
 * @Description: 铁路线表
 * @Version: 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class TableDTO {

    private Long sourceId;

    private String tableName;

}

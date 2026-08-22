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
public class FieldDTO {

    private Long tableId;


    private String fieldName;


    private String type;


    private Boolean idKey;


    private Boolean dateKey;


    private Boolean lesseeKey;


    private Boolean dataFlag;

    private String idFormationStrategy;

    private String idKeyFrom;

    private String fieldNameDefault;

    private String aliasName;

    private Boolean dataDelFlag;

    private String dataDelFlagDefault;

}

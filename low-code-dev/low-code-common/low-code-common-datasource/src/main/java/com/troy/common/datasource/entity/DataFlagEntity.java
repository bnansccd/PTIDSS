package com.troy.common.datasource.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sym
 * @since 2024/6/19 15:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFlagEntity extends TBaseEntity {

    private Integer dataFlag = 0;

}

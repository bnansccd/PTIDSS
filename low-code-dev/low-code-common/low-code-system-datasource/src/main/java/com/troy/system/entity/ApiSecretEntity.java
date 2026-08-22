package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sym
 * @description
 * @date 2023/11/30 17:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("t_sys_oauth_secret")
public class ApiSecretEntity extends TBaseEntity {

    private String orgId;

    private String orgKey;

}

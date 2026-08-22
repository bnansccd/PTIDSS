package com.troy.system.api.domain.DTO;

import lombok.Data;

/**
 * @author chenxl
 * @description
 * @date 2024-08-20 11:51
 */
@Data
public class ApiSecretEntity {

    private String orgId;

    private String orgKey;

    private Long tenantId;

}

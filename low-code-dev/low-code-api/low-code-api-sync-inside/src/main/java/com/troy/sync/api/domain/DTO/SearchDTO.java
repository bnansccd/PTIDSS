package com.troy.sync.api.domain.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-06-28 11:29
 */
@Data
public class SearchDTO {

    private TableDTO tableEntity;

    private List<FieldDTO> list;

    private Long tenantId;

    private Date beginTime;

    private Date endTime;

}

package com.troy.form.domain.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author chenxl
 * @date 2023/11/10
 */
@Data
public class PageDTO {

    private String name;

    private String code;

    private Long databaseId;

    private Long sort;

    private PageListDTO pageListDTO;

    private List<PageColumnDTO> columnList;

    private List<PageSearchDTO> searchList;

    private List<PageButtonDTO> buttonList;

    private List<PageParamDTO> paramList;

}

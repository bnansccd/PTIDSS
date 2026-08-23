package com.troy.form.domain.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 *  实体类。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
@Data
public class DbRelationDTO {


    /**
     * E-R模型ID
     */
    private Long erId;

    /**
     * 表ID
     */
    @NotNull(message = "表不能为空！")
    private Long tableId;

    /**
     * 列ID
     */
    @NotNull(message = "列不能为空！")
    private Long columnId;

    /**
     * 父级主键ID
     */
    private Long parentId;

    /**
     * 表ID （上一级）
     */
    private Long relationTableId;

    /**
     * 列ID （上一级）
     */
    private Long relationColumnId;

    /**
     * 1 单表 2一对一 3一对多
     */
    private String type;

    /**
     * 对应信息
     */
    private List<DbRelationDTO> dbRelationList;
}

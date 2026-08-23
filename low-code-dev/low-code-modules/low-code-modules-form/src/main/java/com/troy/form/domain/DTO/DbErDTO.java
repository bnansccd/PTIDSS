package com.troy.form.domain.DTO;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 *  实体类。
 *
 * @author zhuqing
 * @since 2023-10-19 14:19:33
 */
@Data
public class DbErDTO {

    /**
     * 模型名称
     */
    @NotBlank(message = "er模型名称不能为空！")
    private String name;

    /**
     * er模型标识
     */
    @NotBlank(message = "er模型名称不能为空！")
    private String erModelMark;

    @NotNull(message = "app标识不能为空！")
    private Long appId;

    @Valid
    @NotNull(message = "表关系不存在！")
    private DbRelationDTO dbRelationDto;

}

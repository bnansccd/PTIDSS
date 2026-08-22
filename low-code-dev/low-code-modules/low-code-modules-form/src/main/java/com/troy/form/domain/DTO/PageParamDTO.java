package com.troy.form.domain.DTO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Data
public class PageParamDTO {

    private Long id;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 入参标识
     */
    private String paramMark;

    /**
     * 入参描述
     */
    private String paramDesc;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 非空校验
     */
    private String isCheckNull;

    /**
     * 默认值
     */
    private String defaultValue;

}

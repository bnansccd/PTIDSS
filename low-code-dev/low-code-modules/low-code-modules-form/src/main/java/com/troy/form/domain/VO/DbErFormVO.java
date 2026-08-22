package com.troy.form.domain.VO;

import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-09 09:45:50
 */
@Data
public class DbErFormVO {

    /**
     * 表单
     */
    private String name;

    /**
     * 标识
     */
    private String mark;

    /**
     * 版本
     */
    private String edition;

    /**
     * er关系模型Id
     */
    private Long erId;

    private String erName;

    /**
     * 排序类型
     */
    private Integer sort;

    /**
     * 应用ID
     */
    private Long appId;

}

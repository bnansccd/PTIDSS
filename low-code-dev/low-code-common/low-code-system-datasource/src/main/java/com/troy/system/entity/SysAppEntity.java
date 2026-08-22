package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Data;

/**
 * <p>
 * 公司产品表
 * </p>
 *
 * @author chenxl
 * @since 2023-03-14
 */
@Data
@Table(value = "t_sys_app")
public class SysAppEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否启用
     */
    private String status;

    /**
     * 链接地址
     */
    private String url;

    /**
     * APP名称
     */
    private String name;

    /**
     * 应用编码
     */
    private String code;

    /**
     * 背景图
     */
    private String background;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 应用类型1内部应用2外部应用
     */
    private String type;

    /**
     * 外部应用密钥
     */
    private String secret;
}

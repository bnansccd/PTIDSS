package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * <p>
 * 岗位管理
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_post")
public class SysPostEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 岗位名称
     */
    @Column("post_name")
    private String postName;

    /**
     * 岗位code
     */
    @Column("post_code")
    private String postCode;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    /**
     * 岗位描述
     */
    @Column("remarks")
    private String remarks;

    private String sfqy;
}

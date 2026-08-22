package com.troy.system.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * <p>
 * 部门管理
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_depart")
public class SysDepartEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Column("depart_name")
    private String departName;

    /**
     * 上级部门Id
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    /**
     * 祖级列表
     */
    @Column("ancestors")
    private String ancestors;

    /**
     * 管理人员ID
     */
    @Column("user_id")
    private Long userId;

    @Column("sys_target")
    private String sysTarget;

    /**
     * 单位代码
     */
    private String code;


    private String sfqy;
}

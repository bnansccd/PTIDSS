package com.troy.system.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Data;

/**
 * <p>
 * 字典类型
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_dict")
public class SysDictEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典名称
     */
    @Column("dict_name")
    private String dictName;

    /**
     * 字典类型
     */
    @Column("dict_type")
    private String dictType;

    /**
     * 字典父类型
     */
    @Column("parent_type")
    private String parentType;

    /**
     * 字典父id
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    /**
     * 备注
     */
    @Column("remarks")
    private String remarks;
}

package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * <p>
 * 角色管理
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_role")
public class SysRoleEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @Column(value = "role_name",comment = "角色名称")
    @Consistency
    private String roleName;

    /**
     * 角色编码
     */
    @Column(value = "role_code",comment = "角色编码")
    @Consistency
    private String roleCode;

    /**
     * 数据范围 1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限
     */
    @Column(value = "data_range",comment = "数据范围 1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
    @Consistency
    private String dataRange;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    @Column("remark")
    private String remark;

    private String isSuper;

}

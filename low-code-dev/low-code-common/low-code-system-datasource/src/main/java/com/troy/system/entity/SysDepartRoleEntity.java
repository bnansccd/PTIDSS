package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 部门与角色关系表用与数据权限
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_depart_role")
public class SysDepartRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(value = "depart_id")
    private Long departId;

    @Column(value = "role_id")
    private Long roleId;
}

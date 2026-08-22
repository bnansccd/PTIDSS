package com.troy.system.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色菜单关系表
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_role_menu")
public class SysRoleMenuEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @Column(value = "role_id")
    private Long roleId;

    /**
     * 菜单id
     */
    @Column(value = "menu_id")
    private Long menuId;
}

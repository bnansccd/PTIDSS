package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.*;

import java.io.Serializable;

/**
 * 基础模块应用表 实体类。
 *
 * @author echo
 * @since 2026-02-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_role_app_menu")
public class RoleAppMenuEntity extends TBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long appId;

    private Long roleId;

    private Long menuId;

}

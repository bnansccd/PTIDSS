package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.annotation.Consistency;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Data;

/**
 * <p>
 * 菜单管理
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_menu")
public class SysMenuEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    @Column(value = "menu_name",comment = "菜单名称")
    @Consistency
    private String menuName;

    /**
     * 菜单类型（0左侧菜单1顶部菜单2按钮）
     */
    @Column("menu_type")
    private String menuType;

    /**
     * 图标
     */
    @Column("icon")
    private String icon;

    /**
     * 路由地址
     */
    @Column(value ="href", comment = "路由地址")
    @Consistency
    private String href;

    /**
     * 父级菜单id
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 父级菜单编码
     */
    @Column("parent_menu_code")
    private String parentMenuCode;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;

    /**
     * 权限标识
     */
    @Column("menu_code")
    private String menuCode;

    /**
     * 启用停用(0启用1停用)
     */
    @Column(value = "status", onInsertValue = "0")
    private String status;

    /**
     * 是否展示(0展示1隐藏)
     */
    @Column(value = "is_show", onInsertValue = "0")
    private String isShow;

    /**
     * 祖籍
     */
    @Column("ancestors")
    private String ancestors;

    /**
     * 祖籍编码
     */
    @Column("ancestors_code")
    private String ancestorsCode;

    /**
     * 应用id
     */
    @Column(value = "app_id")
    private Long appId;

    /**
     * 应用编码
     */
    @Column(value = "app_code")
    private String appCode;

    /**
     * 是否是基础菜单 0是 1否
     */
    @Column(value = "is_base", onInsertValue = "0")
    private String isBase;
}

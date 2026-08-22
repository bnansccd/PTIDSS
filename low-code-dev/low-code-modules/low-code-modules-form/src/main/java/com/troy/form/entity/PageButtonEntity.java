package com.troy.form.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_form_page_button")
public class PageButtonEntity extends TBaseEntity {

    /**
     * 操作类型 1新增
        2发起流程
        3编辑
        4流程详细
        5详细
        6批量删除
        7删除
        8刷新
        9导出
        10导入
        11查看子表
        12批量收藏
        13自定义
     */
    private String type;

    /**
     * 按键名称
     */
    private String name;

    /**
     * 按键类型 1工具栏 2行内
     */
    private String buttonPostion;

    /**
     * 按键风格 1primary
        2default
        3dashed
        4danger
        5link
     */
    private String buttonStyle;

    /**
     * 打开方式 1默认
        2全屏弹框
        3大屏弹框
        4中屏弹框
        5小屏弹框
        6新页签打开
        7本页签覆盖

     */
    private String openStyle;

    /**
     * 按钮图标
     */
    private String buttonIcon;

    /**
     * 关联表单ID
     */
    private Long formId;

    /**
     * 关联页面ID
     */
    private Long pageId;

    /**
     * er模型ID
     */
    private Long erId;

    /**
     * 展示条件
     */
    private String showCondition;

    /**
     * 按钮是否平铺
     */
    private String isButtonTile;

    /**
     * 是否权限设置
     */
    private String isPermission;

    /**
     * 权限标识
     */
    private String permissionMark;

    /**
     * 自定义脚本
     */
    private String customScript;

    /**
     * 前处理脚本
     */
    private String beforeScript;

    /**
     * 后处理脚本
     */
    private String afterScript;

}

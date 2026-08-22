package com.troy.form.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * @author chenxl
 * @date 2023/10/18
 */
@Data
@Table(value = "t_form_db_column")
public class DbColumnEntity extends TBaseEntity {
    
    @Column(value = "column_name")
    private String columnName;

    @Column(value = "column_type")
    private String columnType;

    @Column(value = "column_key")
    private String columnKey;

    @Column(value = "data_type")
    private String dataType;

    @Column(value = "system_data_type")
    private String systemDataType;

    @Column(value = "character_maximum_length")
    private Integer characterMaximumLength;

    @Column(value = "character_octet_length")
    private Integer characterOctetLength;

    @Column(value = "numeric_precision")
    private Integer numericPrecision;

    @Column(value = "numeric_scale")
    private Integer numericScale;

    @Column(value = "is_nullable")
    private String isNullable;

    @Column(value = "column_default")
    private String columnDefault;

    @Column(value = "column_comment")
    private String columnComment;

    @Column(value = "table_id")
    private Long tableId;

    @Column(ignore = true)
    private String tableSchema;

    private Integer decimals;

    private Long sort;

    private String alterName;

    private String alterType;

    /**
     * 1 新增 2更新 3删除 4不变更
     */
    private String status;

    /**
     * 类型
     1 隐藏
     2 单行文本
     3 多行文本
     4 数字
     5 下拉框
     6 复选框
     7 单选框
     8 开关
     9 日期
     10 时间
     11 评分
     12 滑动条
     13 流水号
     14 富文本
     15 图标选择
     16 附件上传
     17 图片上传
     18 用户选择
     19 部门选择
     20 弹框选择
     21 签名
     22 树选择
     23 级联
     */
    private String type;

    /**
     * 是否必选
     */
    private String required;

    /**
     校验规则
     1邮箱地址
     2手机号码
     3数字
     4字母或下划线
     5首字字母,最长18,仅包含字母、数字、下划线
     6网址
     7汉字
     8QQ号
     9以字母开头
     10整数
     11正整数
     12日期
     13时间
     14邮政编码
     15身份证
     16固定电话
     */
    private String validated;

    /**
     数据转换
     1不转换
     2时间格式
     3静态选项Code转名称
     4数据字典Code转名称
     5通过Sql配置转换
     6通过服务配置转换
     7用户ID转名称
     8用户Code转名称
     9部门ID转名称
     10部门Code转名称
     11通过页面模型进行数据转换
     12以图片形式展示
     13以附件形式展示
     14以超链接形式展示
     15以开关形式展示
     16以html形式展示
     */
    private String convert;

    private String typeConfig;

    private String convertConfig;
}

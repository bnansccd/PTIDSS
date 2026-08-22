package com.troy.form.domain.DTO;


import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.security.annotation.ValidDict;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenxl
 * @date 2023/11/2
 */
@Data
public class TableColumnDTO {

    private Long id;

    @NotBlank(message = "字段名称不能为空")
    private String columnName;

    @NotBlank(message = "字段数据类型不能为空")
    @ValidDict(parentType = DictTypeEnums.TABLE_DATA_TYPE, message = "支持的数据类型")
    private String systemDataType;

    private Integer length;

    private Integer scale;

    @NotBlank(message = "是否必填不能为空")
    private String isNullable;

    private String columnDefault;

    private String columnComment;

    /**
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

    private String typeConfig;

    /**
     * 是否必选
     */
    private String required;

    /**
     多个逗号隔开
     校验规则 1邮箱地址
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
     * 数据转换
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

    private String convertConfig;

}

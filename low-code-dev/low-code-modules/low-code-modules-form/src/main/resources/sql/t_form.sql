/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.88.128
 Source Server Type    : MySQL
 Source Server Version : 50742 (5.7.42)
 Source Host           : 192.168.74.128:3306
 Source Schema         : form

 Target Server Type    : MySQL
 Target Server Version : 50742 (5.7.42)
 File Encoding         : 65001

 Date: 09/11/2023 11:16:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_form_app
-- ----------------------------
DROP TABLE IF EXISTS `t_form_app`;
CREATE TABLE `t_form_app`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称\r\n',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `sort` int(5) NULL DEFAULT NULL COMMENT '排序',
  `type_id` bigint(20) NULL DEFAULT NULL COMMENT '类型ID',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `common` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否公共',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_app_type
-- ----------------------------
DROP TABLE IF EXISTS `t_form_app_type`;
CREATE TABLE `t_form_app_type`  (
  `id` bigint(20) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驱动',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用分类管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_column_convert
-- ----------------------------
DROP TABLE IF EXISTS `t_form_column_convert`;
CREATE TABLE `t_form_column_convert`  (
  `id` bigint(20) NOT NULL,
  `relation_id` bigint(20) NULL DEFAULT NULL COMMENT '支持多种',
  `expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表达式',
  `format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '时间格式化',
  `json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置静态属性',
  `dict_id` bigint(20) NULL DEFAULT NULL COMMENT '字典ID',
  `sql_id` bigint(20) NULL DEFAULT NULL COMMENT 'sqlId',
  `service_id` bigint(20) NULL DEFAULT NULL COMMENT '服务ID',
  `view_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '页面编码',
  `view_code_id` bigint(20) NULL DEFAULT NULL COMMENT '页面Id',
  `store_id` bigint(20) NULL DEFAULT NULL COMMENT '储存ID',
  `show_id` bigint(20) NULL DEFAULT NULL COMMENT '展示Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表字段数据转换' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db`;
CREATE TABLE `t_form_db`  (
  `id` bigint(20) NOT NULL,
  `identification` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源标识',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源类型',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源类型',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据url',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库用户',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库密码',
  `driver` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驱动',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_column
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_column`;
CREATE TABLE `t_form_db_column`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `column_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名',
  `column_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `data_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `system_data_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统字段类型',
  `character_maximum_length` int(2) NULL DEFAULT NULL COMMENT '字段长度',
  `is_nullable` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为空 YES, NO',
  `column_key` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为主键',
  `column_default` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `column_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表ID',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `decimals` int(20) NULL DEFAULT NULL COMMENT '小数点长度',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 隐藏\r\n2 单行文本\r\n3 多行文本 \r\n4 数字     \r\n5 下拉框\r\n6 复选框 \r\n7 单选框\r\n8 开关\r\n9 日期\r\n10 时间\r\n11 评分\r\n12 滑动条\r\n13 流水号\r\n14 富文本\r\n15 图标选择\r\n16 附件上传\r\n17 图片上传\r\n18 用户选择\r\n19 部门选择\r\n20 弹框选择\r\n21 签名\r\n22 树选择\r\n23 级联',
  `convert` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据转换\r\n1不转换\r\n2时间格式\r\n3静态选项Code转名称\r\n4数据字典Code转名称\r\n5通过Sql配置转换\r\n6通过服务配置转换\r\n7用户ID转名称\r\n8用户Code转名称\r\n9部门ID转名称\r\n10部门Code转名称\r\n11通过页面模型进行数据转换\r\n12以图片形式展示\r\n13以附件形式展示\r\n14以超链接形式展示\r\n15以开关形式展示\r\n16以html形式展示\r\n',
  `validated` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '校验规则 1邮箱地址\r\n2手机号码\r\n3数字\r\n4字母或下划线\r\n5首字字母,最长18,仅包含字母、数字、下划线\r\n6网址\r\n7汉字\r\n8QQ号\r\n9以字母开头\r\n10整数\r\n11正整数\r\n12日期\r\n13时间\r\n14邮政编码\r\n15身份证\r\n16固定电话',
  `required` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必选',
  `type_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '类型参数',
  `convert_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '转换配置',
  `alter_name` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否变更名称 ',
  `alter_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否变更类型',
  `sort` int(5) NULL DEFAULT NULL,
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1不变更 2新增 3更新 4删除 ',
  `numeric_precision` int(2) NULL DEFAULT NULL,
  `numeric_scale` int(2) NULL DEFAULT NULL,
  `character_octet_length` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库表字段信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_column_cascade
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_column_cascade`;
CREATE TABLE `t_form_db_column_cascade`  (
  `id` bigint(20) NOT NULL,
  `column_form_id` bigint(20) NULL DEFAULT NULL COMMENT '列ID',
  `multiple` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否多选',
  `view_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '页面编码',
  `view_code_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `store_id` bigint(20) NULL DEFAULT NULL COMMENT '储存ID',
  `show_id` bigint(20) NULL DEFAULT NULL COMMENT '展示ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表字段级联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_column_form
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_column_form`;
CREATE TABLE `t_form_db_column_form`  (
  `id` bigint(20) NOT NULL,
  `column_id` bigint(20) NULL DEFAULT NULL COMMENT '列ID',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 隐藏\r\n2 单行文本\r\n3 多行文本 \r\n4 数字     \r\n5 下拉框\r\n6 复选框 \r\n7 单选框\r\n8 开关\r\n9 日期\r\n10 时间\r\n11 评分\r\n12 滑动条\r\n13 流水号\r\n14 富文本\r\n15 图标选择\r\n16 附件上传\r\n17 图片上传\r\n18 用户选择\r\n19 部门选择\r\n20 弹框选择\r\n21 签名\r\n22 树选择\r\n23 级联',
  `required` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必选',
  `validated` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '校验规则 1邮箱地址\r\n2手机号码\r\n3数字\r\n4字母或下划线\r\n5首字字母,最长18,仅包含字母、数字、下划线\r\n6网址\r\n7汉字\r\n8QQ号\r\n9以字母开头\r\n10整数\r\n11正整数\r\n12日期\r\n13时间\r\n14邮政编码\r\n15身份证\r\n16固定电话',
  `convert` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据转换\r\n1不转换\r\n2时间格式\r\n3静态选项Code转名称\r\n4数据字典Code转名称\r\n5通过Sql配置转换\r\n6通过服务配置转换\r\n7用户ID转名称\r\n8用户Code转名称\r\n9部门ID转名称\r\n10部门Code转名称\r\n11通过页面模型进行数据转换\r\n12以图片形式展示\r\n13以附件形式展示\r\n14以超链接形式展示\r\n15以开关形式展示\r\n16以html形式展示\r\n',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表字段页面属性' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_er
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_er`;
CREATE TABLE `t_form_db_er`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型名称',
  `er_model_mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'er模型标识',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型标识类型， 1 单表 2一对一 3一对多',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库E-R信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_er_form
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_er_form`;
CREATE TABLE `t_form_db_er_form`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单',
  `mark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识',
  `edition` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本',
  `er_id` bigint(20) NULL DEFAULT NULL COMMENT 'er关系模型Id',
  `sort` int(5) NULL DEFAULT NULL COMMENT '排序类型',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用ID',
  `is_lock` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否上锁',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '上锁人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功能表单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_er_form_register
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_er_form_register`;
CREATE TABLE `t_form_db_er_form_register`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单',
  `mark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识',
  `edition` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本',
  `er_id` bigint(20) NULL DEFAULT NULL COMMENT '表单Id',
  `sort` int(5) NULL DEFAULT NULL COMMENT '排序类型',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功能表单注册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_er_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_er_relation`;
CREATE TABLE `t_form_db_er_relation`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `er_id` bigint(20) NULL DEFAULT NULL COMMENT 'E-R模型ID',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表ID',
  `column_id` bigint(20) NULL DEFAULT NULL COMMENT '列ID',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父级主键ID',
  `relation_table_id` bigint(20) NULL DEFAULT NULL COMMENT '表ID （上一级）',
  `relation_column_id` bigint(20) NULL DEFAULT NULL COMMENT '列ID （上一级）',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 单表 2一对一 3一对多',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库E-R关系信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_sql
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_sql`;
CREATE TABLE `t_form_db_sql`  (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'sql编码',
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'key',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'text',
  `db_id` bigint(20) NULL DEFAULT NULL,
  `sort` int(5) NULL DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_db_table
-- ----------------------------
DROP TABLE IF EXISTS `t_form_db_table`;
CREATE TABLE `t_form_db_table`  (
  `id` bigint(20) NOT NULL,
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名称',
  `engine` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表引擎',
  `table_comment` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用ID',
  `is_created` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否创建',
  `update_status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否更新',
  `db_id` bigint(20) NULL DEFAULT NULL COMMENT '数据库ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库表管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_form_dict`;
CREATE TABLE `t_form_dict`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `dict_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `parent_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典父类型',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '字典父id',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `remarks` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `ancestors` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '祖籍列表',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT 'appId',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = DYNAMIC TABLESPACE = `innodb_system`;

-- ----------------------------
-- Table structure for t_form_flow_no
-- ----------------------------
DROP TABLE IF EXISTS `t_form_flow_no`;
CREATE TABLE `t_form_flow_no`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成规则名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成规则编码',
  `clazz` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成规则实现类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流水号生成规则配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page`;
CREATE TABLE `t_form_page`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '页面名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '页面code',
  `db_id` bigint(20) NULL DEFAULT NULL COMMENT '数据源ID',
  `type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 列表页面 2属性页面 3移动页面 4 组合页面',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面模型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_action
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_action`;
CREATE TABLE `t_form_page_action`  (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `operation_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 编辑 2流程详情 3详情 4 查看子表 5自定义',
  `open_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1默认 2 全屏 3大 4中 5小',
  `form_id` bigint(20) NULL DEFAULT NULL COMMENT '表单ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `import_export_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '出入参数配置',
  `before_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '前处理脚本',
  `after_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '后处理脚本',
  `custom_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义脚本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '列表动作配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_button
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_button`;
CREATE TABLE `t_form_page_button`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作类型 1新增\r\n2发起流程\r\n3编辑\r\n4流程详细\r\n5详细\r\n6批量删除\r\n7删除\r\n8刷新\r\n9导出\r\n10导入\r\n11查看子表\r\n12批量收藏\r\n13自定义',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按键名称',
  `button_postion` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按键类型 1工具栏 2行内',
  `button_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按键风格 1primary\r\n2default\r\n3dashed\r\n4danger\r\n5link',
  `open_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '打开方式 1默认\r\n2全屏弹框\r\n3大屏弹框\r\n4中屏弹框\r\n5小屏弹框\r\n6新页签打开\r\n7本页签覆盖\r\n',
  `button_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮图标',
  `form_id` bigint(20) NULL DEFAULT NULL COMMENT '关联表单ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '关联页面ID',
  `er_id` bigint(20) NULL DEFAULT NULL COMMENT 'er模型ID',
  `show_condition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '展示条件',
  `is_button_tile` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮是否平铺',
  `is_permission` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否权限设置',
  `permission_mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `custom_script` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义脚本',
  `before_script` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前处理脚本',
  `after_script` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后处理脚本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面案件设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_column
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_column`;
CREATE TABLE `t_form_page_column`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `column_mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列标识',
  `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列别名',
  `merge_header_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合并表头名',
  `column_data_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型 1字符串 2大文本 3二进制 4数字型 5日期',
  `align` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对齐方式 1 居左 2居种 3居右',
  `min_width` int(3) NULL DEFAULT NULL COMMENT '最小宽度',
  `fixed_column` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '固定列 1 不固定 2固定居左 3固定居右',
  `is_desensitization` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否 脱敏1无\r\n2邮箱地址\r\n3手机号码\r\n4身份证\r\n5地址\r\n6银行卡号\r\n',
  `is_import` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否导入',
  `is_export` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否导出',
  `export_high` int(4) NULL DEFAULT NULL COMMENT '导出高度',
  `is_export_auto_wrap` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自动换行',
  `is_export_hide` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否隐藏',
  `is_sort` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否支持排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面模型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_list
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_list`;
CREATE TABLE `t_form_page_list`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `custom_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义SQL',
  `order_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '排序sql',
  `custom_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义弹窗标题',
  `search` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否默认查询',
  `check_box` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否复选框',
  `line_no` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否行号',
  `column_width` int(3) NULL DEFAULT NULL COMMENT '宽度',
  `fixed_operation_column` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否固定操作页',
  `fixed_flow_column` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否流程列',
  `page` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否分页',
  `page_num` int(3) NULL DEFAULT NULL COMMENT '分页数',
  `roll` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否可以滚动',
  `tree` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否树形',
  `tree_parent_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '树父节点字段',
  `tree_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '树节点字段',
  `flowable` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否流程',
  `flowable_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程范围',
  `forbid_flowabe_column` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否禁用流程状态列',
  `forbid_current_column` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否禁用当前步骤列',
  `custom_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义行样式',
  `custom_cells_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义单元格样式',
  `custom_merge_function` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义合并函数',
  `excel_title_high` int(3) NULL DEFAULT NULL COMMENT '标题高度',
  `excel_head_high` int(3) NULL DEFAULT NULL COMMENT '表头高度',
  `excel_cell_high` int(3) NULL DEFAULT NULL COMMENT '表格高度',
  `custom_class` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义class',
  `slop_head` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头部插槽',
  `slop_tail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '尾部插槽',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面列表页配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_param
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_param`;
CREATE TABLE `t_form_page_param`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `param_mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入参标识',
  `param_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入参描述',
  `param_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数类型',
  `is_check_null` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '非空校验',
  `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面入参声明' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_page_search
-- ----------------------------
DROP TABLE IF EXISTS `t_form_page_search`;
CREATE TABLE `t_form_page_search`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(2) NULL DEFAULT 0 COMMENT '删除标识（0未删1删除）',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `column_id` bigint(20) NULL DEFAULT NULL COMMENT '列ID',
  `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `query_operators` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询操作符',
  `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认查询值',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型',
  `is_expand` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认展开',
  `custom_sort` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定制排序',
  `custom_sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义sql',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面查询条件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_service_request
-- ----------------------------
DROP TABLE IF EXISTS `t_form_service_request`;
CREATE TABLE `t_form_service_request`  (
  `id` bigint(20) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务标识',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'url',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 get 2 post 3 put 4 delete ',
  `json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'json格式',
  `sort` int(5) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务请求配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_form_service_request_param
-- ----------------------------
DROP TABLE IF EXISTS `t_form_service_request_param`;
CREATE TABLE `t_form_service_request_param`  (
  `id` bigint(20) NOT NULL,
  `requset_id` bigint(50) NULL DEFAULT NULL COMMENT '服务编码',
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务标识',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '变更人',
  `del_flag` tinyint(2) NULL DEFAULT NULL COMMENT '删除标识',
  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',
  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '请求表单配置' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

package com.troy.sync.util;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 17:18
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableStructureToInsertSQL {
    public static void main(String[] args) {
        String tableStructure = "CREATE TABLE `tr_t_jgxw_check_action`  (\n" +
                "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
                "  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',\n" +
                "  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',\n" +
                "  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',\n" +
                "  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人',\n" +
                "  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',\n" +
                "  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',\n" +
                "  `del_flag` int(11) NULL DEFAULT 0 COMMENT '删除标识',\n" +
                "  `version` bigint(20) NULL DEFAULT 0 COMMENT '版本号',\n" +
                "  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',\n" +
                "  `record_unique_identity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '记录唯一标识',\n" +
                "  `supervise_item_implement_code` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监管事项目录编码',\n" +
                "  `supervise_item_check_icode` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监管事项检查实施清单编码',\n" +
                "  `check_action_name` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检查行为名称',\n" +
                "  `check_action_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检查行为编号',\n" +
                "  `implement_institution` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实施机构',\n" +
                "  `implement_institution_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实施机构编码',\n" +
                "  `entrust_dept` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '受委托部门',\n" +
                "  `entrust_dept_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '受委托部门编码',\n" +
                "  `supervise_object` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监管对象',\n" +
                "  `administrative_cp` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人（单位/个人）名称',\n" +
                "  `administrative_cp_na` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人性质',\n" +
                "  `administrative_cp_ce_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人证件类型',\n" +
                "  `administrative_cp_ui_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人（单位/个人）编码',\n" +
                "  `address_registered` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册地址',\n" +
                "  `address_operating` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '经营地址',\n" +
                "  `area_number` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政区划代码',\n" +
                "  `check_form` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检查形式',\n" +
                "  `check_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检查类别',\n" +
                "  `check_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检查方式',\n" +
                "  `check_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '检查结果',\n" +
                "  `check_date` datetime NULL DEFAULT NULL COMMENT '检查时间',\n" +
                "  `check_personnel` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '检查人员',\n" +
                "  `check_personnel_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执法证号',\n" +
                "  `submitted_date` datetime NULL DEFAULT NULL COMMENT '报送时间',\n" +
                "  `submitted_personnel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报送人员',\n" +
                "  `plan_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '双随机一公开计划id',\n" +
                "  `cd_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主键（部门编码主键）',\n" +
                "  `cd_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插入时间',\n" +
                "  `cd_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源（省份区划代码引部门编码）',\n" +
                "  `cd_batch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次号,规则为日期+次数，例如2019年4月11日第二次提交数据，批次为2019041100002',\n" +
                "  `cd_operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新增数据类型i-insert,u-update,d-delete',\n" +
                "  `company_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业分类',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ")";

        String insertStatements = generateInsertStatements(tableStructure);
        System.out.println(insertStatements);
    }

    private static String generateInsertStatements(String tableStructure) {
        StringBuilder insertSQL = new StringBuilder();
        insertSQL.append("INSERT INTO `t_sync_data_table_field` ")
                .append("(`table_id`, `field_name`, `type`, `id_key`, `date_key`, `lessee_key`, `data_flag`, `id_formation_strategy`, `id_key_from`)\nVALUES\n");

        Pattern pattern = Pattern.compile("`(\\w+)`\\s+(\\w+\\(\\d+\\)|\\w+)\\s+.*COMMENT\\s+'(.*?)'.*");
        Matcher matcher = pattern.matcher(tableStructure);

        boolean first = true;

        while (matcher.find()) {
            if (!first) {
                insertSQL.append(",\n");
            } else {
                first = false;
            }

            String fieldName = matcher.group(1);
            String fieldType = matcher.group(2);
            String comment = matcher.group(3);

            String idKey = "0";
            String dateKey = "0";
            String lesseeKey = "0";
            String dataFlag = "0";

            if (fieldName.equals("id")) {
                idKey = "1";
            } else if (fieldName.contains("date") || fieldName.contains("time")) {
                dateKey = "1";
            }

            insertSQL.append(String.format("(NULL, '%s', '%s', '%s', '%s', '%s', '%s', NULL, NULL)",
                    fieldName, fieldType, idKey, dateKey, lesseeKey, dataFlag));
        }

        insertSQL.append(";");

        return insertSQL.toString();
    }
}


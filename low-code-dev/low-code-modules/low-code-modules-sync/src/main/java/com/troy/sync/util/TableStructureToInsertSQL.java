package com.troy.sync.util;

/**
 * @author chenxl
 * @description
 * @date 2024-06-20 17:18
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableStructureToInsertSQL {
    public static void main(String[] args) {
        String sql = "CREATE TABLE `tr_t_gajhgx_xzcfxxzb_new`  (\n" +
                "  `id` bigint(20) NOT NULL COMMENT 'ID',\n" +
                "  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户',\n" +
                "  `create_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者',\n" +
                "  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',\n" +
                "  `modify_id` bigint(20) NULL DEFAULT NULL COMMENT '修改者',\n" +
                "  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',\n" +
                "  `del_flag` tinyint(4) NULL DEFAULT NULL COMMENT '删除标识',\n" +
                "  `create_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',\n" +
                "  `modify_depart_id` bigint(20) NULL DEFAULT NULL COMMENT '修改部门',\n" +
                "  `version` bigint(20) NULL DEFAULT 0 COMMENT '锁字段',\n" +
                "  `recid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'tid',\n" +
                "  `cf_xdr_mc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人名称',\n" +
                "  `cf_xdr_lb` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人类别',\n" +
                "  `cf_xdr_shxym` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人代码_1(统一社会信用代码)',\n" +
                "  `cf_xdr_gszc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人代码_2(工商注册号)',\n" +
                "  `cf_xdr_zzjg` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人代码_3(组织机构代码)',\n" +
                "  `cf_xdr_swdj` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人代码_4(税务登记号)',\n" +
                "  `cf_xdr_sydw` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政相对人代码_5(事业单位证书号)',\n" +
                "  `cf_frdb` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法定代表人',\n" +
                "  `cf_fr_zjlx` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法定代表人证件号码',\n" +
                "  `cf_fr_sfzh` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法定代表人证件号码',\n" +
                "  `cf_xdr_zjlx` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自然人证件类型',\n" +
                "  `cf_xdr_zjhm` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自然人证件号码',\n" +
                "  `cf_wsh` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政处罚决定书文号',\n" +
                "  `cf_wfxw` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '违法行为类型',\n" +
                "  `cf_sy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '违法事实',\n" +
                "  `cf_yj` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '处罚依据',\n" +
                "  `cf_cflb` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处罚类别',\n" +
                "  `cf_nr` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '处罚内容',\n" +
                "  `cf_nr_fk` decimal(18, 6) NULL DEFAULT NULL COMMENT '罚款金额（万元）',\n" +
                "  `cf_nr_wfff` decimal(18, 6) NULL DEFAULT NULL COMMENT '没收违法所得、没收非法财物的金额（万元）',\n" +
                "  `cf_nr_zkdx` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '暂扣或吊销证照名称及编号',\n" +
                "  `cf_jdrq` datetime NULL DEFAULT NULL COMMENT '处罚决定日期',\n" +
                "  `cf_yxq` datetime NULL DEFAULT NULL COMMENT '处罚有效期',\n" +
                "  `cf_gsjzq` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '公示截止期',\n" +
                "  `cf_cfjg` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处罚机关',\n" +
                "  `cf_cfjgdm` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处罚机关统一社会信用代码',\n" +
                "  `cf_sjly` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据来源单位',\n" +
                "  `cf_sjlydm` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据来源单位统一社会信用代码',\n" +
                "  `bz` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',\n" +
                "  `cf_cfws` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,\n" +
                "  `cf_cfwsh` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,\n" +
                "  `cf_xdr_shzz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,\n" +
                "  `lysj` datetime NULL DEFAULT NULL COMMENT '对方推送数据时间',\n" +
                "  `shzzdjzh` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '社会组织登记证号',\n" +
                "  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',\n" +
                "  `wf_lb` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '违法类别 1 非法营运 2 极限超载',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ")";


        System.out.println(getMySQL(sql));

    }

    private static String getMySQL(String tableStructure) {
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

    public static String getOracleSql(String oracleCreateTableScript) {
        List<String> insertStatements = new ArrayList<>();

        // 找到表名
        Pattern tableNamePattern = Pattern.compile("CREATE TABLE\\s+\"\\w+\"\\.\"(\\w+)\"");
        Matcher tableNameMatcher = tableNamePattern.matcher(oracleCreateTableScript);
        String tableName = tableNameMatcher.find() ? tableNameMatcher.group(1) : "UNKNOWN_TABLE";

        // 提取列定义部分（排除 CONSTRAINT 部分）
        int startIndex = oracleCreateTableScript.indexOf('(') + 1;
        int endIndex = oracleCreateTableScript.indexOf("CONSTRAINT");
        if (endIndex == -1) {
            endIndex = oracleCreateTableScript.lastIndexOf(')');
        }
        String columnDefinitions = oracleCreateTableScript.substring(startIndex, endIndex).trim();

        Pattern pattern = Pattern.compile("\"(\\w+)\"\\s+(\\w+)(?:\\((\\d+)\\s*\\w*\\))?");
        Matcher matcher = pattern.matcher(columnDefinitions);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String dataType = matcher.group(2);
            String length = matcher.group(3);

            String type = mapDataType(dataType, length);
            String insertStatement = String.format(
                    "INSERT INTO `t_sync_data_table_field` " +
                            "(`table_id`, `field_name`, `type`, `id_key`, `date_key`, `lessee_key`, `data_flag`) " +
                            "VALUES (NULL, '%s', '%s', 0, 0, 0, 0);",
                    fieldName, type
            );
            insertStatements.add(insertStatement);
        }

        return insertStatements.stream().collect(Collectors.joining("\n"));
    }

    private static String mapDataType(String oracleType, String length) {
        switch (oracleType.toUpperCase()) {
            case "VARCHAR2":
                return "varchar(" + (length != null ? length : "255") + ")";
            case "DATE":
                return "datetime";
            default:
                return oracleType.toLowerCase();
        }
    }
}


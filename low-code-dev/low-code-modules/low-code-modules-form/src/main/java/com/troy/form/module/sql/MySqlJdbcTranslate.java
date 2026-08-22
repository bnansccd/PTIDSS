package com.troy.form.module.sql;

import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbColumnEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public class MySqlJdbcTranslate extends JdbcTranslate {

    private static final String FIND_COLUMN = "SELECT * FROM information_schema.columns WHERE table_schema = (SELECT DATABASE()) and table_name = '{0}'";

    private static final String FIND_TABLE= "SELECT table_name, table_comment, ENGINE FROM information_schema.tables WHERE table_schema = (SELECT DATABASE()) and table_name = '{0}'";

    private static final String FIND_ALL_TABLES = "SELECT table_name, table_comment, ENGINE FROM information_schema.tables WHERE table_schema = (SELECT DATABASE())";

    /**
     * 数据库字符串类型
     */
    public static final String[] COLUMN_TYPE_VARCHAR = {"char", "varchar", "nvarchar", "varchar2"};

    /**
     * 数据库文本类型
     */
    public static final String[] COLUMN_TYPE_TEXT = {"tinytext", "text", "mediumtext", "longtext"};

    /**
     * 数据库时间类型
     */
    public static final String[] COLUMN_TYPE_TIME = {"datetime", "time", "date", "timestamp"};

    /**
     * 数据库数字类型
     */
    public static final String[] COLUMN_TYPE_NUMBER = {"tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bigint", "float", "double", "decimal"};

    private static final HashMap<String, String> TYPES = new HashMap<>();

    static {
        Arrays.asList(COLUMN_TYPE_VARCHAR).forEach(e-> TYPES.put(e, DictValueEnums.VARCHAR.getCode()));

        Arrays.asList(COLUMN_TYPE_TEXT).forEach(e-> TYPES.put(e, DictValueEnums.TEXT.getCode()));

        Arrays.asList(COLUMN_TYPE_TIME).forEach(e-> TYPES.put(e, DictValueEnums.DATE.getCode()));

        Arrays.asList(COLUMN_TYPE_NUMBER).forEach(e-> TYPES.put(e, DictValueEnums.NUMBER.getCode()));
    }

    @Override
    public List<DbTableEntity> getAllTable(DatasourceEntity datasource, String... params) {
        return  (List<DbTableEntity>) JdbcHelper.executeSql(datasource, FIND_ALL_TABLES, DbTableEntity.class);
    }

    @Override
    public DbTableEntity getCurrentTable(DatasourceEntity datasource, String... params) {
        List list = JdbcHelper.executeSql(datasource, getSQL(FIND_TABLE, params), DbTableEntity.class);
        if (StringUtils.isNotEmpty(list)){
            return  (DbTableEntity)list.get(0);
        }
        return null;
    }

    @Override
    public List<DbColumnEntity> getCurrentTableColumn(DatasourceEntity datasource, String... params) {
        List<DbColumnEntity> list = JdbcHelper.executeSql(datasource, getSQL(FIND_COLUMN, params), DbColumnEntity.class);
        if (StringUtils.isNotEmpty(list)){
            list.forEach(e-> e.setSystemDataType(TYPES.get(e.getDataType())));
        }
        return list;
    }

    @Override
    public String generateSql(List<DbColumnEntity> list, DbTableEntity entity) {
        // 卸载语句
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS `");
        sb.append(entity.getTableName());
        sb.append("`;");

        sb.append("CREATE TABLE `");
        sb.append(entity.getTableName());
        sb.append("`(");

        List<DbColumnEntity> priKeys = list.stream().filter(e -> {
            if (StringUtils.isNotBlank(e.getColumnKey())){
                if (DictValueEnums.PRI.getCode().equals(e.getColumnKey().toLowerCase())){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        if (StringUtils.isEmpty(priKeys)){
            throw new ServiceException(ResultEnum.ERROR, "该表未包含主键");
        }

        list.forEach(e->{
            sb.append("`");
            sb.append(e.getColumnName());
            sb.append("` ");

            sb.append(e.getColumnType());
            sb.append(" ");
            if (DictValueEnums.VARCHAR.getCode().equals(e.getSystemDataType()) || DictValueEnums.TEXT.getCode().equals(e.getSystemDataType())){
                sb.append("CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ");
            }
            if (StringUtils.isNotBlank(e.getColumnKey()) && DictValueEnums.PRI.getCode().equals(e.getColumnKey().toLowerCase())){
                sb.append("NOT NULL ");
            } else {
                sb.append("NULL ");

                sb.append("DEFAULT ");
                if (StringUtils.isNotBlank(e.getColumnDefault())){
                    sb.append(e.getColumnDefault());
                    sb.append(" ");
                } else {
                    sb.append("NULL ");
                }
            }
            if (StringUtils.isNotBlank(e.getColumnComment())){
                sb.append("COMMENT '");
                sb.append(e.getColumnComment());
                sb.append("' ");
            }
            sb.append(", ");
        });

        sb.append("PRIMARY KEY (");
        priKeys.forEach(e->{
            sb.append("`");
            sb.append(e.getColumnName());
            sb.append("`,");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        sb.append(" USING BTREE");
        sb.append(") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;");
        return sb.toString();
    }
}

package com.troy.common.datasource.listener;

import com.mybatisflex.codegen.dialect.impl.MySqlJdbcDialect;
import com.mybatisflex.codegen.dialect.impl.OracleJdbcDialect;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.impl.DB2105Dialect;
import com.mybatisflex.core.dialect.impl.DmDialect;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.ObjectUtil;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.api.model.LoginUser;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * @author sym
 * @since 2025/4/21 下午4:40
 */
public class MyLogicDeleteProcessor extends DefaultLogicDeleteProcessor {
    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect dialect) {

        String sql = dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicDeletedValue());

        //扩展一下软删除的sql语句，增加删除时间和删除人。
        if (BaseEntity.class.isAssignableFrom(tableInfo.getEntityClass())) {
            if (SecurityContextHolder.getUserId() != 0L) {
                sql += "," + dialect.wrap("modify_id") + EQUALS + SecurityContextHolder.getUserId();
            }
            sql += "," + dialect.wrap("modify_time") + EQUALS + getTimeFunction(dialect);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isNotNull(loginUser)) {
                SysUserVO sysUserVO = loginUser.getSysUserVO();
                if (StringUtils.isNotNull(sysUserVO) && StringUtils.isNotNull(sysUserVO.getDepartId())) {
                    sql += "," + dialect.wrap("modify_depart_id") + EQUALS + sysUserVO.getDepartId();
                }
            }
        }

        return sql;
    }

    private static Object prepareValue(Object value) {
        return !(value instanceof Number) && !(value instanceof Boolean) ? "'" + value + "'" : value;
    }

    private static String getTimeFunction(IDialect dialect) {
        DbType dbType = DialectFactory.getHintDbType();
        switch (dbType) {
            case MYSQL:
                return "now()";
            case MARIADB:
                return "now()";
            case ORACLE:
                return "SYSDATE";
            case ORACLE_12C:
                return "SYSDATE";
            case DB2:
                return "CURRENT_TIMESTAMP";
            case DB2_1005:
                return "CURRENT_TIMESTAMP";
            case H2:
                return "CURRENT_TIMESTAMP";
            case HSQL:
                return "CURRENT_TIMESTAMP";
            case SQLITE:
                return "datetime('now')";
            case POSTGRE_SQL:
                return "now()";
            case SQLSERVER:
                return "GETDATE()";
            case SQLSERVER_2005:
                return "GETDATE()";
            case DM:
                return "SYSDATE";
            case XUGU:
                return "now()";
            case KINGBASE_ES:
                return "now()";
            case PHOENIX:
                return "now()";
            case GAUSS:
                return "now()";
            case CLICK_HOUSE:
                return "now()";
            case GBASE:
                return "now()";
            case GBASE_8S:
                return "now()";
            case OSCAR:
                return "SYSDATE";
            case SYBASE:
                return "now()";
            case OCEAN_BASE:
                return "SYSDATE";
            case FIREBIRD:
                return "CURRENT_TIMESTAMP";
            case DERBY:
                return "CURRENT_TIMESTAMP";
            case HIGH_GO:
                return "CURRENT_TIMESTAMP";
            case CUBRID:
                return "CURRENT_TIMESTAMP";
            case GOLDILOCKS:
                return "CURRENT_TIMESTAMP";
            case CSIIDB:
                return "CURRENT_TIMESTAMP";
            case SAP_HANA:
                return "CURRENT_TIMESTAMP";
            case IMPALA:
                return "CURRENT_TIMESTAMP";
            case VERTICA:
                return "CURRENT_TIMESTAMP";
            case XCloud:
                return "CURRENT_TIMESTAMP";
            case REDSHIFT:
                return "SYSDATE";
            case OPENGAUSS:
                return "now()";
            case TDENGINE:
                return "NOW";
            case INFORMIX:
                return "CURRENT";
            case SINODB:
                return "CURRENT_TIMESTAMP";
            case UXDB:
                return "CURRENT_TIMESTAMP";
            case GREENPLUM:
                return "now()";
            case LEALONE:
                return "now()";
            case HIVE:
                return "CURRENT_TIMESTAMP()";
            case DORIS:
                return "CURRENT_TIMESTAMP()";
            default:
                return "now()";
        }
    }
}

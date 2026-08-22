package com.troy.common.core.utils.sql;

import com.troy.common.core.exception.UtilException;
import com.troy.common.core.utils.StringUtils;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;


/**
 * @Author ZhuQing
 * @Date: 2022/7/6  13:17
 * sql操作工具类
 */
public class SqlUtil {
    /**
     * 定义常用的 sql关键字
     */
    public static String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords) {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }

    /**
     * 判断文本是否为合法、单条SQL语句
     * @param sql
     * @return
     */
    public static boolean isValidSingleSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }
        try {
            // parse仅解析单条语句，含分号多条会抛异常
            CCJSqlParserUtil.parse(sql);
            return true;
        } catch (Exception e) {
            // 语法错误、多条SQL、非SQL文本都会捕获
            return false;
        }
    }

    /**
     * 在合法单SQL前提下，判断是否仅SELECT查询
     * @param sql
     * @return
     */
    public static boolean isOnlySelectSql(String sql) {
        if (!isValidSingleSql(sql)) {
            return false;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            // 语法树根节点为Select代表纯查询
            return statement instanceof Select;
        } catch (Exception e) {
            return false;
        }
    }
}


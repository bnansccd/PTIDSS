package com.troy.form.module.sql;

import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.domain.DTO.DatasourceDTO;
import com.troy.form.entity.DatasourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @date 2023/11/6
 */
public class JdbcHelper{

    private static final Logger log = LoggerFactory.getLogger(JdbcHelper.class);

    private static final List<String> LIST = new ArrayList<>(Arrays.asList("update", "delete", "where", "insert", "from", ";", "--"));

    private static final HashMap<String, JdbcTranslate> TRANSLATIONS = new HashMap<>();

    static {
        TRANSLATIONS.put("mysql", new MySqlJdbcTranslate());
    }


    private static final Integer NUMBER_DOWN = 1;

    private static final Integer NUMBER_UP = 65;

    private static final Integer NUMBER_SCALE_UP = 30;

    private static final Integer NUMBER_SCALE_DOWN = 1;

    public static JdbcTranslate getTranslate(DatasourceEntity entity){
        JdbcTranslate translate = TRANSLATIONS.get(entity.getType());
        if (translate == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据支持类型");
        }
        return translate;
    }

    public static boolean isKey(String tableName){
        return LIST.stream().anyMatch(e-> tableName.contains(e.toLowerCase()));
    }

    public static boolean judgeParameterRange(String systemDataType, Integer length, Integer scale){
        if (DictValueEnums.NUMBER.getCode().equals(systemDataType)){
            if (length != null && scale != null){
                if ((length > NUMBER_UP || length < NUMBER_DOWN) || (scale > NUMBER_SCALE_UP || scale < NUMBER_SCALE_DOWN)){
                    return true;
                }
                if (scale.compareTo(length) >= 0){
                    return false;
                }
            }
            return length == null || length == 0;
        } else if (DictValueEnums.BINARY.getCode().equals(systemDataType)){
            return length == null || length == 0;
        } else {
            return false;
        }
    }

    public static String getDbType(String url) {
        String pattern = "(.*?):(.*?):";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            DictValueEnums code = DictValueEnums.findByTypeAndCode(DictTypeEnums.DATA_BASE.getCode(), StringUtils.lowerCase(m.group(2)));
            if (code == null){
                throw new ServiceException(ResultEnum.NO_RANGE, "数据库类型");
            } else {
                return code.getCode();
            }
        } else {
            throw new ServiceException(ResultEnum.ERROR, "输入数据库类型");
        }
    }

    public static void judge(DatasourceDTO dto){
        // 加载驱动程序
        try {
            Class.forName(dto.getDriver());
        } catch (ClassNotFoundException e) {
            throw new ServiceException(ResultEnum.ERROR, "支持驱动");
        }

        // 建立数据库连接
        try (Connection connection = DriverManager.getConnection(dto.getUrl(), dto.getUsername(), dto.getPassword())) {
            log.info("数据库{}连接正常", dto.getUrl());
        } catch (SQLException e) {
            throw new ServiceException(ResultEnum.ERROR, "连接数据库");
        }
    }

//    public static void main(String[] args) {
//        DatasourceEntity entity = new DatasourceEntity();
//        entity.setType("mysql");
//        entity.setUsername("root");
//        entity.setPassword("zdwy123456");
//        entity.setUrl("jdbc:mysql://192.168.74.128:3306/camunda?characterEncoding=UTF-8&useUnicode=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai");
//        entity.setDriver("com.mysql.cj.jdbc.Driver");
//        List list = executeSql(entity, "SELECT * from test WHERE", DatasourceEntity.class);
//        System.out.println(list);
//    }

    public static List executeSql(DatasourceEntity entity, String sql, Class clazz) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(entity.getDriver());
            // 打开链接
            log.info("连接数据库...");
            conn = DriverManager.getConnection(entity.getUrl() , entity.getUsername() ,entity.getPassword());

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();

            List list = new ArrayList<>();
            List<String> collect = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
            while(rs.next()){
                Object o = clazz.newInstance();

                for(int i = 1; i <= colCount; i++){
                    String colName = metaData.getColumnName(i);

                    Object value = rs.getObject(i);
                    String camelCase = StringUtils.toCamelCase(colName.toLowerCase());
                    if (collect.contains(camelCase)){
                        // 使用反射给对象属性赋值
                        Field field = clazz.getDeclaredField(camelCase);
                        field.setAccessible(true);

                        if (value instanceof BigInteger){
                            field.set(o,  ((BigInteger) value).intValue());
                        } else {
                            field.set(o, value);
                        }

                    }
                }

                list.add(o);
            }
            return list;
        } catch(Exception se){
            // 处理 JDBC 错误
            throw new ServiceException(se.getMessage());
        } finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
                se2.printStackTrace();
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

}

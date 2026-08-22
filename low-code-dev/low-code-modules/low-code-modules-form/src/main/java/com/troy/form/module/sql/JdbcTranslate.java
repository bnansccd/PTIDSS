package com.troy.form.module.sql;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.module.form.FormHelper;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbColumnEntity;
import com.troy.system.api.domain.VO.SysUserVO;
import com.troy.system.api.model.LoginUser;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @date 2023/11/8
 */
public abstract class JdbcTranslate {


    static final HashMap<String, String> DEFAULT = new HashMap<>();

    static {
        DEFAULT.put(DictValueEnums.VARCHAR.getCode(), "2");
        DEFAULT.put(DictValueEnums.TEXT.getCode(), "3");
        DEFAULT.put(DictValueEnums.NUMBER.getCode(), "4");
        DEFAULT.put(DictValueEnums.DATE.getCode(), "9");
    }

    /**
     * 获取所有表
     * @return
     */
    public List<DbTableEntity> getAllTable(DatasourceEntity datasource, String... params){
        return null;
    }

    /**
     * 获取当前表结构的表信息
     * @return
     */
    public DbTableEntity getCurrentTable(DatasourceEntity datasource, String... params){
        return null;
    }

    /**
     * 获取当前表结构的列结构
     * @return
     */
    List<DbColumnEntity> getCurrentTableColumn(DatasourceEntity datasource, String... params){
        return null;
    }


    public String generateSql(List<DbColumnEntity> list, DbTableEntity entity){
        return null;
    }

    /**
     * 获取插入sql
     * @param list
     * @param entity
     * @param dataList
     * @return
     */
    public int insertSql(List<DbColumnEntity> list, DbTableEntity entity, List<JSONObject> dataList){
        judge(list, dataList);

        Date date = new Date();
        Long createId = null;
        Long createDepartId = null;
        Long tenantId = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)){
            SysUserVO sysUserVO = loginUser.getSysUserVO();
            if (StringUtils.isNotNull(sysUserVO)){
                createId = sysUserVO.getId();
                createDepartId = sysUserVO.getDepartId();
                tenantId = sysUserVO.getTenantId();
            }
        }

        List<Row> result = new ArrayList<>();
        Long finalCreateId = createId;
        Long finalCreateDepartId = createDepartId;
        Long finalTenantId = tenantId;
        dataList.forEach(e->{
            Row row = new Row();
            list.forEach(x->{
                if (e.get(x.getColumnName()) == null){
                    row.set(x.getColumnName(), null);
                } else {
                    row.set(x.getColumnName(), e.get(x.getColumnName()));
                }

                if ("create_time".equals(x.getColumnName())){
                    row.set(x.getColumnName(), date);
                }
                if ("create_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalCreateId);
                }
                if ("create_depart_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalCreateDepartId);
                }
                if ("tenant_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalTenantId);
                }
            });
            result.add(row);
        });
        int[] batch = Db.insertBatch(entity.getTableName(), result);
        return batch.length;
    }

    /**
     * 更新sql
     * @param list
     * @param entity
     * @param dataList
     * @return
     */
    public int updateSql(List<DbColumnEntity> list, DbTableEntity entity, List<JSONObject> dataList){
        judge(list, dataList);

        Date date = new Date();
        Long createId = null;
        Long createDepartId = null;
        Long tenantId = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)){
            SysUserVO sysUserVO = loginUser.getSysUserVO();
            if (StringUtils.isNotNull(sysUserVO)){
                createId = sysUserVO.getId();
                createDepartId = sysUserVO.getDepartId();
                tenantId = sysUserVO.getTenantId();
            }
        }

        List<Row> result = new ArrayList<>();

        Long finalCreateId = createId;
        Long finalCreateDepartId = createDepartId;
        Long finalTenantId = tenantId;
        dataList.forEach(e->{
            Row row = Row.ofKey(RowKey.AUTO);
            list.forEach(x->{
                if (e.get(x.getColumnName()) == null){
                    row.set(x.getColumnName(), null);
                } else {
                    row.set(x.getColumnName(), e.get(x.getColumnName()));
                }

                if ("modify_time".equals(x.getColumnName())){
                    row.set(x.getColumnName(), date);
                }
                if ("modify_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalCreateId);
                }
                if ("modify_depart_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalCreateDepartId);
                }
                if ("tenant_id".equals(x.getColumnName())){
                    row.set(x.getColumnName(), finalTenantId);
                }
            });
            result.add(row);
        });

        return Db.updateBatchById(entity.getTableName(), result);
    }

    /**
     * 假删除
     * @param entity
     * @param list
     * @return
     */
    public int deleteByFlag(DbTableEntity entity, Collection<Serializable> list){
        Date date = new Date();
        Long createId = null;
        Long createDepartId = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)){
            SysUserVO sysUserVO = loginUser.getSysUserVO();
            if (StringUtils.isNotNull(sysUserVO)){
                createId = sysUserVO.getId();
                createDepartId = sysUserVO.getDepartId();
            }
        }

        List<Row> result = new ArrayList<>();

        Long finalCreateId = createId;
        Long finalCreateDepartId = createDepartId;
        list.forEach(e->{
            Row row = Row.ofKey(RowKey.AUTO);
            row.set("id", e);
            row.set("del_flag", Constants.ONE);
            row.set("modify_time", date);
            row.set("modify_id", finalCreateId);
            row.set("modify_depart_id", finalCreateDepartId);
            result.add(row);
        });

        return Db.updateBatchById(entity.getTableName(), result);
    }


    /**
     * 真删除
     * @param columnEntity
     * @param entity
     * @param list
     * @return
     */
    public int deleteSql(DbColumnEntity columnEntity, DbTableEntity entity, Collection<Serializable> list){
        return Db.deleteBatchByIds(entity.getTableName(), columnEntity.getColumnName(), list);
    }

    void judge(List<DbColumnEntity> list, List<JSONObject> dataList){
        List<String> collect = list.stream().filter(e -> Constants.NO.equals(e.getIsNullable())).map(DbColumnEntity::getColumnName).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(collect)){
            dataList.forEach(e-> collect.forEach(x->{
                Object o = e.get(x);
                if (o == null){
                    throw new ServiceException(ResultEnum.NOT_FOUND, x);
                }
            }));
        }
    }

    /**
     * 获取真实类型
     * @param datasource
     * @param params
     * @return
     */
    public List<DbColumnEntity> getDbColumns(DatasourceEntity datasource, String... params){
        List<DbColumnEntity> column = getCurrentTableColumn(datasource, params);
        if (StringUtils.isNotEmpty(column)){
            column.forEach(e->{
                String s = DEFAULT.get(e.getSystemDataType());
                String json = FormHelper.getDefaultJSON(e.getSystemDataType());
                e.setTypeConfig(json);
                e.setType(s);
                e.setConvert(DictValueEnums.CONVERT_NOT.getCode());
            });
        }
        return column;
    }

    /**
     * sql转换
     * @param sql
     * @param args
     * @return
     */
    String getSQL(String sql, String... args) {
        Matcher matcher = Pattern.compile("\\{.+?\\}").matcher(sql);
        int index = 0;
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(sb, args[index++]);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}

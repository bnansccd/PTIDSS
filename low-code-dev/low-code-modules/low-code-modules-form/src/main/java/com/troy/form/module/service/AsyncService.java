package com.troy.form.module.service;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.Constants;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.module.entity.sql.DmlEntity;
import com.troy.form.module.entity.sql.RowEntity;
import com.troy.form.entity.DatasourceEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author chenxl
 * @date 2023/11/10
 */
@Service
public class AsyncService {

    @Async("asyncExecutor")
    public Future<List<JSONObject>> executeListSql(DatasourceEntity entity, String sql, List<RowEntity> list, Long tenantId){
        try {
            DataSourceKey.use(tenantId+ "_" + entity.getIdentification());
            List<Row> rows = Db.selectListBySql(sql);

            List<JSONObject> result = new ArrayList<>();
            rows.forEach(e->{
                JSONObject object = new JSONObject();
                list.forEach(x->{
                    if (Constants.STRING.equals(x.getType())){
                        String string = e.getString(x.getName());
                        object.put(x.getName(), string);
                    }
                    if (Constants.LONG.equals(x.getType())){
                        Long aLong = e.getLong(x.getName());
                        object.put(x.getName(), aLong);
                    }
                    if (Constants.BYTES.equals(x.getType())){
                        byte[] bytes = e.getBytes(x.getName());
                        object.put(x.getName(), bytes);
                    }
                    if (Constants.DECIMAL.equals(x.getType())){
                        BigDecimal decimal = e.getBigDecimal(x.getName());
                        object.put(x.getName(), decimal);
                    }
                    if (Constants.DATE.equals(x.getType())){
                        Date date = e.getDate(x.getName());
                        object.put(x.getName(), date);
                    }
                });

                result.add(object);
            });

            return new AsyncResult<>(result);
        } finally {
            DataSourceKey.clear();
        }
    }

    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class)
    public Future<Integer> executeInsertSql(DatasourceEntity entity, List<List<DmlEntity>> list, DbTableEntity table, Long tenantId) throws InterruptedException {
        try {
            DataSourceKey.use(tenantId+ "_" + entity.getIdentification());

            List<Row> rowList = new ArrayList<>();
            list.forEach(e->{
                Row row = new Row();
                e.forEach(x->{
                    row.put(x.getName(), x.getValue());
                });
                rowList.add(row);
            });
            int[] ints = Db.insertBatch(table.getTableName(), rowList);
            return new AsyncResult<>(ints.length);
        } finally {
            DataSourceKey.clear();
        }
    }

}

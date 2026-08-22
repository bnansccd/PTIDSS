package com.troy.sync.service.impl;

import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.troy.sync.api.domain.DTO.FieldDTO;
import com.troy.sync.api.domain.DTO.SearchDTO;
import com.troy.sync.service.SyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class SyncServiceImpl implements SyncService {

    @Override
    public List<Row> sync(String tableName, SearchDTO empty) {
        List<FieldDTO> fieldList = empty.getList();

        QueryCondition empty1 = QueryCondition.createEmpty();
        Optional<FieldDTO> delFlag1 = fieldList.stream().filter(e->{
            if(e.getDataDelFlag() != null){
                return e.getDataDelFlag();
            }
            return false;
        }).findFirst();
        if (delFlag1.isPresent()){
            FieldDTO entity = delFlag1.get();
            QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
            empty1.and(condition);
        }

        Optional<FieldDTO> lessKey = fieldList.stream().filter(e->e.getLesseeKey() != null && e.getLesseeKey()).findFirst();
        if (lessKey.isPresent() && empty.getTenantId() != null){
            FieldDTO entity = lessKey.get();
            QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, empty.getTenantId());
            empty1.and(condition);
        }

        List<Row> rows = Db.selectListByCondition(tableName, empty1);
        updateTime(rows);
        return rows;
    }

    @Override
    public List<Row> getSyncIncrease(String tableName, SearchDTO syncDTO) {
        List<FieldDTO> fieldEntities = syncDTO.getList();

        QueryWrapper wrapper = QueryWrapper.create();

        Optional<FieldDTO> delFlag = fieldEntities.stream().filter(e->{
            if(e.getDataDelFlag() != null){
                return e.getDataDelFlag();
            }
            return false;
        }).findFirst();
        if (delFlag.isPresent()){
            FieldDTO entity = delFlag.get();
            QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
            wrapper.and(condition);
        }

        Optional<FieldDTO> first = fieldEntities.stream().filter(FieldDTO::getLesseeKey).findFirst();
        if (first.isPresent() && syncDTO.getTenantId() != null){
            FieldDTO entity = first.get();
            QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getTenantId());
            wrapper.and(condition);
        }

        List<FieldDTO> collect = fieldEntities.stream().filter(FieldDTO::getDateKey).collect(Collectors.toList());

        QueryColumn column = new QueryColumn(collect.get(0).getFieldName());
        QueryCondition condition = column.between(syncDTO.getBeginTime(), syncDTO.getEndTime());
        for (int i = 1; i < collect.size(); i++) {
            condition = condition.or(new QueryColumn(collect.get(i).getFieldName()).between(syncDTO.getBeginTime(), syncDTO.getEndTime()));
        }
        wrapper.and(condition);

        List<Row> rows = Db.selectListByQuery(syncDTO.getTableEntity().getTableName(), wrapper);
        updateTime(rows);
        return rows;
    }

    @Override
    public List<Row> findByScript(String script) {
        List<Row> rows = Db.selectListBySql(script);
        updateTime(rows);
        return rows;
    }



    public void updateTime(List<Row> list){
        list.forEach(e->{
            Set<String> set = e.keySet();
            set.forEach(x->{
                try {
                    Object data = e.get(x);
                    if (data instanceof oracle.sql.TIMESTAMP){
                        oracle.sql.TIMESTAMP timestamp = (oracle.sql.TIMESTAMP) data;
                        long time = timestamp.timestampValue().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format = sdf.format(time);
                        e.set(x, format);
                    }
                    if (data instanceof java.sql.Timestamp){
                        Timestamp timestamp = (Timestamp) data;
                        e.set(x, TimestampToStr(timestamp));
                    }

                    // CF_YJ -> {NClobProxyImpl@14782} "oracle.sql.NCLOB@16b7b0ea"
                    if (data instanceof com.alibaba.druid.proxy.jdbc.NClobProxyImpl){
                        NClobProxyImpl nclob = ((com.alibaba.druid.proxy.jdbc.NClobProxyImpl) data);
                        long length = nclob.length();

                        // 截取NClob中的字符串，从第1个字符开始，截取长度为length的字符串
                        String value = nclob.getSubString(1, (int) length);

                        e.set(x, value);
                    }

                    if (data instanceof oracle.sql.NCLOB){
                        oracle.sql.NCLOB nclob = ((oracle.sql.NCLOB) data);
                        StringBuilder stringBuilder = new StringBuilder();
                        try (
                                // 获取 NCLOB 的字符流
                                Reader reader = nclob.getCharacterStream();
                                BufferedReader bufferedReader = new BufferedReader(reader);
                        ) {
                            String line;
                            // 逐行读取字符流并追加到 StringBuilder 中
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                        } catch (IOException a) {
                            a.printStackTrace();
                        }
                        e.set(x, stringBuilder);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
    }

    public static String TimestampToStr(Timestamp timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        if (isThirteenDigitTimestamp(timestamp.getTime())) {
            return sdf.format(new Date(timestamp.getTime()));
        }
        return sdf.format(new Date(timestamp.getTime() * 1000L));
    }

    public static boolean isThirteenDigitTimestamp(long timestamp) {
        return String.valueOf(timestamp).length() == 13;
    }

}

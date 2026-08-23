package com.troy.sync;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.HttpUtil;
import com.troy.sync.dao.FieldDao;
import com.troy.sync.entity.FieldEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.troy.sync.service.impl.SyncServiceImpl.getAliasRow;
import static com.troy.sync.service.impl.SyncServiceImpl.getRows;

/**
 * @author sym
 * @since 2024/7/5 15:40
 */
@SpringBootTest
@Slf4j
public class ImportTest {

    @Autowired
    private FieldDao fieldDao;

    @Test
    void sync(){

        String done = HttpUtil.doPostJson("https://gajt.gazhcs.com:11433/cxl/api/rpc/v1/getSyncByScript", "select * from \"GAJHGX\".\"T_YGJ_SD_PUBLIC_P1_BASEINFO\"", null);
        JSONObject object = JSON.parseObject(done);
        JSONArray data = object.getJSONArray("data");
        List<Row> list = new ArrayList<>();
        for (Object datum : data) {
            JSONObject jsonObject = (JSONObject) datum;
            Row row = new Row();
            row.putAll(jsonObject);
            list.add(row);
        }

        List<FieldEntity> fieldList = fieldDao.findByTableId(31L);
        getAliasRow(fieldList, list);
        List<FieldEntity> toFieldList = fieldDao.findByTableId(30L);
        List<Row> rowList = getRows(toFieldList,list, 63841664354013187L, null);
        Db.insertBatch("tr_t_user_info", rowList, Constants.ONE_THOUSAND * Constants.TEN);

    }

}

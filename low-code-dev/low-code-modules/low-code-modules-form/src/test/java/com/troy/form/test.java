package com.troy.form;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.module.entity.sql.RowEntity;
import com.troy.form.module.service.AsyncService;
import com.troy.form.module.sql.JdbcHelper;
import com.troy.form.module.sql.JdbcTranslate;
import com.troy.form.service.DatasourceService;
import com.troy.form.dao.DbColumnDao;
import com.troy.form.dao.DbTableDao;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbColumnEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @Auther: zhuqing
 * @Date: 2022/10/9 17:17:30
 * @Description: test
 * @Version: 1.0.0
 */
@SpringBootTest
public class test {

//    @Autowired
//    private AnylineService service;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private DbColumnDao columnDao;

    @Autowired
    private DbTableDao dbTableDao;


    @Test
//    @Transactional(rollbackFor = Exception.class)
    void ss(){
        SecurityContextHolder.setTenantId(63841664354013184L);

        DbTableEntity table = dbTableDao.getById(79764329806876672L);
        List<DbColumnEntity> columnList = columnDao.findByTableId(79764329806876672L);

        try {
            DataSourceKey.use(63841664354013184L + "_" + "xxxx1");
            List<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                JSONObject object = new JSONObject();
                object.put("id", i);
                object.put("lxmc", i);
                object.put("lxbm", i);
                list.add(object);
            }

            DatasourceEntity entity = new DatasourceEntity();
            entity.setType("mysql");
            JdbcTranslate translate = JdbcHelper.getTranslate(entity);
            translate.insertSql(columnList, table, list);
        }  finally {
            DataSourceKey.clear();
        }
    }


    @Test
    void context() throws Exception {
        DatasourceEntity dto = new DatasourceEntity();
        dto.setDriver("com.mysql.cj.jdbc.Driver");
        dto.setIdentification("xxxx2");
        dto.setName("xxxx2");
        dto.setUrl("jdbc:mysql://192.168.88.193:3306/form?characterEncoding=UTF-8&useUnicode=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai");
        dto.setUsername("root");
        dto.setPassword("zdwy123456");

        List<RowEntity> list = new ArrayList<>();
        RowEntity rowEntity1 = new RowEntity();
        rowEntity1.setName("column_name");
        rowEntity1.setType(Constants.STRING);

        RowEntity rowEntity2 = new RowEntity();
        rowEntity2.setName("column_type");
        rowEntity2.setType(Constants.STRING);

        RowEntity rowEntity3 = new RowEntity();
        rowEntity3.setName("data_type");
        rowEntity3.setType(Constants.STRING);

        RowEntity rowEntity4 = new RowEntity();
        rowEntity4.setName("character_maximum_length");
        rowEntity4.setType(Constants.LONG);

        list.add(rowEntity1);
        list.add(rowEntity2);
        list.add(rowEntity3);
        list.add(rowEntity4);

        Future<List<JSONObject>> future = asyncService.executeListSql(dto, "select * from t_form_db_column", list, 63841664354013184L);
        List<JSONObject> jsonObjects = future.get();
        System.out.println(jsonObjects);
    }
}

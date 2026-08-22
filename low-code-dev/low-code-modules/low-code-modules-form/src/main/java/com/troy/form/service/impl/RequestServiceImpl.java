package com.troy.form.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.config.IdWorkGenerator;
import com.troy.form.dao.*;
import com.troy.form.entity.*;
import com.troy.form.service.RequestService;
import com.troy.form.dao.*;
import com.troy.form.domain.DTO.FormDTO;
import com.troy.form.entity.*;
import com.troy.form.module.form.FormHelper;
import com.troy.form.module.sql.JdbcHelper;
import com.troy.form.module.sql.JdbcTranslate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenxl
 * @date 2023/11/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RequestServiceImpl implements RequestService {

    @Autowired
    private DbErFormDao dbErFormDao;

    @Autowired
    private DbErDao dbErDao;

    @Autowired
    private DbColumnDao dbColumnDao;

    @Autowired
    private DbErRelationDao dbErRelationDao;

    @Autowired
    private DbTableDao dbTableDao;

    private IdWorkGenerator idGenerator = new IdWorkGenerator();


    @Autowired
    private DatasourceDao datasourceDao;

    @Override
    public void addRequest(FormDTO formDTO) {
        DbErFormEntity form = dbErFormDao.findByMark(formDTO.getMark());
        if (form == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, formDTO.getMark());
        }

        DbErEntity er = dbErDao.getById(form.getErId());
        if (er == null) {
            throw new ServiceException(ResultEnum.NOT_FOUND, "E-R不存在");
        }

        DatasourceEntity datasource = datasourceDao.getById(79009260249489410L);

        List<DbErRelationEntity> list = dbErRelationDao.listByErId(form.getErId());
        if (StringUtils.isEmpty(list)){
            throw new ServiceException(ResultEnum.NOT_FOUND, "E-R列不存在");
        }

        Set<Long> collect = list.stream().map(e -> Arrays.asList(e.getTableId(), e.getRelationTableId())).flatMap(Collection::stream).collect(Collectors.toSet());
        List<DbColumnEntity> dbColumnEntityList = dbColumnDao.findByTableIdIn(collect);

        List<DbTableEntity> tableEntities = dbTableDao.listByIds(collect);

        HashMap<String, List<DbColumnEntity>> params = new HashMap<>();
        tableEntities.forEach(e-> params.put(e.getTableName(), dbColumnEntityList.stream().filter(x->x.getTableId().equals(e.getId())).collect(Collectors.toList())));

        Optional<DbErRelationEntity> first = list.stream().filter(e -> e.getParentId() == null).findFirst();
        if (first.isPresent()){
            Optional<DbTableEntity> dbTableOption = tableEntities.stream().filter(e -> e.getId().equals(first.get().getTableId())).findFirst();
            if (!dbTableOption.isPresent()){
                throw new ServiceException(ResultEnum.ERROR, "E-R模型");
            }

            addTableData(formDTO, params, dbTableOption.get(), datasource);
        } else {
            throw new ServiceException(ResultEnum.ERROR, "E-R模型");
        }
    }

    void judgeValidate(JSONObject object, List<DbColumnEntity> list){
        list.forEach(e->{
            Object o = object.get(e.getColumnName());
            if (Constants.TRUE.equals(e.getRequired())){
                if (o == null || StringUtils.isBlank(o.toString())){
                    throw new ServiceException(ResultEnum.DATA_MUST_FILL, e.getColumnComment());
                }
            }

            if (o != null){
                if (StringUtils.isNotBlank(e.getValidated())){
                    String[] split = e.getValidated().split(Constants.COMMA);
                    for (String validate : split) {
                        DictValueEnums enums = DictValueEnums.findByTypeAndCode(DictTypeEnums.COLUMN_VALIDATE.getCode(), validate);
                        if (!FormHelper.checkData(enums, StringUtils.valueOf(o))){
                            throw new ServiceException(ResultEnum.DATA_INVALID, e.getColumnComment());
                        }
                    }
                }

                if (DictValueEnums.NUMBER.getCode().equals(e.getSystemDataType())){
                    BigDecimal decimal = object.getBigDecimal(e.getColumnName());
                    int precision = decimal.precision();
                    int scale = decimal.scale();

                    Integer numericPrecision = e.getNumericPrecision();
                    Integer numericScale = e.getNumericScale();

                    if (precision > numericPrecision || scale > numericScale){
                        throw new ServiceException(ResultEnum.DATA_LENGTH_INVALID, e.getColumnComment());
                    }
                } else if (DictValueEnums.VARCHAR.getCode().equals(e.getSystemDataType())){
                    String string = object.getString(e.getColumnName());
                    if (StringUtils.isNotBlank(string) && string.length() > e.getCharacterMaximumLength()){
                        throw new ServiceException(ResultEnum.DATA_LENGTH_INVALID, e.getColumnComment());
                    }
                }
            }
        });
    }

    void addTableData(FormDTO formDTO, HashMap<String, List<DbColumnEntity>> params, DbTableEntity dbTable, DatasourceEntity datasource){
        // 处理主表
        JSONObject jsonObject = formDTO.getFormData().getJSONObject(dbTable.getTableName());
        jsonObject.put("id", idGenerator.geId());
        judgeValidate(jsonObject, params.get(dbTable.getTableName()));
        try {
            DataSourceKey.use(SecurityContextHolder.getTenantId() + "_" + datasource.getIdentification());
            JdbcTranslate translate = JdbcHelper.getTranslate(datasource);
            translate.insertSql(params.get(dbTable.getTableName()), dbTable, Collections.singletonList(jsonObject));
        } finally {
            DataSourceKey.clear();
        }
    }
}

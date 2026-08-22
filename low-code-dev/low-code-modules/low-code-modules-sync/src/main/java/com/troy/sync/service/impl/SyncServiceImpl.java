package com.troy.sync.service.impl;

import com.alibaba.excel.util.DateUtils;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.IdWorkUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.sync.api.RemoteSyncInsideService;
import com.troy.sync.api.domain.DTO.FieldDTO;
import com.troy.sync.api.domain.DTO.SearchDTO;
import com.troy.sync.api.domain.DTO.TableDTO;
import com.troy.sync.constants.ErrorConstants;
import com.troy.sync.dao.DatasourceDao;
import com.troy.sync.dao.FieldDao;
import com.troy.sync.dao.TableDao;
import com.troy.sync.domain.DTO.SyncDTO;
import com.troy.sync.domain.DTO.SyncScriptDTO;
import com.troy.sync.entity.DatasourceEntity;
import com.troy.sync.entity.FieldEntity;
import com.troy.sync.entity.TableEntity;
import com.troy.sync.service.LogService;
import com.troy.sync.service.SyncService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class SyncServiceImpl implements SyncService {


    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private TableDao tableDao;

    @Autowired
    private LogService logService;

    @Autowired
    private RemoteSyncInsideService remoteSyncInsideService;

    private static final IdWorkUtils ID_WORK_UTILS = new IdWorkUtils(Constants.ONE, Constants.TWO);

    @Override
    public void sync(SyncDTO syncDTO) {
        long millis = System.currentTimeMillis();

        try {
            SecurityContextHolder.setTenantId(syncDTO.getTenantId());

            List<String> sourceIds = Arrays.asList(syncDTO.getFromTarget(), syncDTO.getToTarget());

            List<DatasourceEntity> list = datasourceDao.findByTargetIn(sourceIds);
            if (new HashSet<>(sourceIds).size() != list.size()){
                throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
            }

            if (Constants.ONE.toString().equals(syncDTO.getType())){
                dealTotal(syncDTO, list);
            } else {
                dealIncrement(syncDTO, list);
            }
        } catch (Exception exception){
            logService.addLog(System.currentTimeMillis()- millis, exception.getMessage(), syncDTO.toString(), false);
            throw exception;
        }
        logService.addLog(System.currentTimeMillis() - millis, null, syncDTO.toString(), true);
    }

    @Override
    public void syncScript(SyncScriptDTO syncDTO) {
        List<String> sourceIds = Arrays.asList(syncDTO.getFromTarget(), syncDTO.getToTarget());

        List<DatasourceEntity> list = datasourceDao.findByTargetIn(sourceIds);
        if (new HashSet<>(sourceIds).size() != list.size()){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity toDatasource = first2.get();


        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        List<Row> fromRows;
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getFromTarget());
            fromRows = Db.selectListBySql(syncDTO.getScript());
        }finally{
            DataSourceKey.clear();
        }

        if (StringUtils.isEmpty(fromRows)){
            return;
        }

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try {
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());
            if (StringUtils.isEmpty(toFieldList)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "表字段未配置完整");
            }
            List<FieldEntity> collect = toFieldList.stream().filter(FieldEntity::getDateKey).collect(Collectors.toList());
            if (StringUtils.isEmpty(collect)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "同步时间字段未配置完整");
            }

            QueryCondition empty = QueryCondition.createEmpty();

            Optional<FieldEntity> delFlag = toFieldList.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                empty.and(condition);
            }

            Optional<FieldEntity> first = toFieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getTenantId());
                empty.and(condition);
            }

            QueryColumn column = new QueryColumn(collect.get(0).getFieldName());
            QueryCondition condition = column.between(syncDTO.getBeginTime(), syncDTO.getEndTime());
            for (int i = 1; i < collect.size(); i++) {
                condition = condition.or(new QueryColumn(collect.get(i).getFieldName()).between(syncDTO.getBeginTime(), syncDTO.getEndTime()));
            }
            empty.and(condition);

            Db.deleteByCondition(toTable.getTableName(), empty);
            List<Row> rowList = getRows(toFieldList, fromRows, syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
            Db.insertBatch(toTable.getTableName(), rowList, Constants.TEN_THOUSAND * Constants.TEN);
        } finally {
            DataSourceKey.clear();
        }
    }

    @Override
    public void dealTotal(SyncDTO syncDTO, List<DatasourceEntity> list) {

        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity formDatasource = first1.get();
        DatasourceEntity toDatasource = first2.get();

        TableEntity fromTable = tableDao.findByTableNameAndSourceId(syncDTO.getFromTable(), formDatasource.getId());
        if (fromTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        List<FieldEntity> fieldList = fieldDao.findByTableId(fromTable.getId());
        List<Row> fromRows;
        try{

            QueryCondition empty = QueryCondition.createEmpty();

            Optional<FieldEntity> delFlag = fieldList.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                empty.and(condition);
            }

            Optional<FieldEntity> first = fieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getFromTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getFromTenantId());
                empty.and(condition);
            }

            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getFromTarget());
            fromRows = Db.selectListByCondition(syncDTO.getFromTable(), empty);
        }finally{
            DataSourceKey.clear();
        }

        if (StringUtils.isEmpty(fromRows)){
            return;
        }

        getAliasRow(fieldList, fromRows);

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());

            QueryCondition empty = QueryCondition.createEmpty();
            Optional<FieldEntity> delFlag = toFieldList.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                empty.and(condition);
            } else {
                empty.and("1 = 1");
            }

            Optional<FieldEntity> first = toFieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getTenantId());
                empty.and(condition);
            }

            // 删除全量
            Db.deleteByCondition(toTable.getTableName(), empty);

            List<Row> rowList = getRows(toFieldList, fromRows, syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
            Db.insertBatch(toTable.getTableName(), rowList, Constants.TEN_THOUSAND * Constants.TEN);
        }finally{
            DataSourceKey.clear();
        }
    }


    @Override
    public void dealIncrement(SyncDTO syncDTO, List<DatasourceEntity> list) {

        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity formDatasource = first1.get();
        DatasourceEntity toDatasource = first2.get();

        TableEntity fromTable = tableDao.findByTableNameAndSourceId(syncDTO.getFromTable(), formDatasource.getId());
        if (fromTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        List<FieldEntity> fieldEntities = fieldDao.findByTableId(fromTable.getId());
        List<Row> fromRows;
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getFromTarget());
            if (StringUtils.isEmpty(fieldEntities)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "表字段未配置完整");
            }
            List<FieldEntity> collect = fieldEntities.stream().filter(FieldEntity::getDateKey).collect(Collectors.toList());
            if (StringUtils.isEmpty(collect)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "同步时间字段未配置完整");
            }

            QueryCondition empty = QueryCondition.createEmpty();

            Optional<FieldEntity> delFlag = fieldEntities.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                empty.and(condition);
            }

            Optional<FieldEntity> first = fieldEntities.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getFromTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getFromTenantId());
                empty.and(condition);
            }

            QueryColumn column = new QueryColumn(collect.get(0).getFieldName());
            QueryCondition condition = column.between(syncDTO.getBeginTime(), syncDTO.getEndTime());
            for (int i = 1; i < collect.size(); i++) {
                condition = condition.or(new QueryColumn(collect.get(i).getFieldName()).between(syncDTO.getBeginTime(), syncDTO.getEndTime()));
            }
            empty.and(condition);

            fromRows = Db.selectListByCondition(syncDTO.getFromTable(), empty);
        }finally{
            DataSourceKey.clear();
        }

        if (StringUtils.isEmpty(fromRows)){
            return;
        }

        // 设置别名
        getAliasRow(fieldEntities, fromRows);

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());

            Optional<FieldEntity> first = fieldEntities.stream().filter(FieldEntity::getIdKey).findFirst();
            if (first.isPresent()){
                FieldEntity entity = first.get();
                Object[] collect;
                if (entity.getAliasName() != null){
                    collect = fromRows.stream().map(e -> e.get(entity.getAliasName()).toString()).toArray();
                } else {
                    collect = fromRows.stream().map(e -> e.get(entity.getFieldName()) == null ? e.get(entity.getFieldName().toLowerCase()).toString() : e.get(entity.getFieldName()).toString()).toArray();
                }

                List<Row> delData = new ArrayList<>();
                QueryCondition query = null;

                QueryCondition condition;
                if (entity.getAliasName() != null){
                    condition = QueryCondition.create(new QueryColumn(entity.getAliasName()), SqlOperator.IN, collect);

                } else {
                    condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.IN, collect);
                }
                query = condition;
                delData = Db.selectListByCondition(toTable.getTableName(), query);

                Optional<FieldEntity> dataFlag = toFieldList.stream().filter(FieldEntity::getDataFlag).findFirst();
                if (dataFlag.isPresent()){
                    FieldEntity field = dataFlag.get();
                    query.and(QueryCondition.create(new QueryColumn(field.getFieldName()), 0));
                }

                Db.deleteByCondition(toTable.getTableName(), query);

                if (StringUtils.isNotEmpty(fromRows)){
                    if (dataFlag.isPresent()){
                        if (entity.getAliasName() != null){
                            List<String> collected = delData.stream().filter(e->Constants.ONE_STR.equals(e.get(dataFlag.get().getFieldName()).toString())).map(e -> e.get(entity.getAliasName()).toString()).collect(Collectors.toList());
                            fromRows.removeIf(e->collected.contains(e.get(entity.getAliasName()).toString()));
                        } else {
                            List<String> collected = delData.stream().filter(e->Constants.ONE_STR.equals(e.get(dataFlag.get().getFieldName()).toString())).map(e -> e.get(entity.getFieldName()).toString()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                            fromRows.removeIf(e->collected.contains(e.get(entity.getFieldName()).toString()));
                        }
                    }
                }
            }

            if (StringUtils.isNotEmpty(fromRows)){
                List<Row> rowList = getRows(toFieldList, fromRows, syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
                Db.insertBatch(toTable.getTableName(), rowList, Constants.TEN_THOUSAND * Constants.TEN);
            }
        }finally{
            DataSourceKey.clear();
        }
    }

    @Override
    public void syncRpc(SyncDTO syncDTO) {
        long millis = System.currentTimeMillis();

        try {
            SecurityContextHolder.setTenantId(syncDTO.getTenantId());

            List<String> sourceIds = Arrays.asList(syncDTO.getFromTarget(), syncDTO.getToTarget());

            List<DatasourceEntity> list = datasourceDao.findByTargetIn(sourceIds);
            if (new HashSet<>(sourceIds).size() != list.size()){
                throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
            }

            if (Constants.ONE.toString().equals(syncDTO.getType())){
                dealTotalRpc(syncDTO, list);
            } else {
                dealIncrementRpc(syncDTO, list);
            }
        } catch (Exception exception){
            logService.addLog(System.currentTimeMillis()- millis, exception.getMessage(), syncDTO.toString(), false);
            throw exception;
        }
        logService.addLog(System.currentTimeMillis() - millis, null, syncDTO.toString(), true);
    }

    @Override
    public void dealTotalRpc(SyncDTO syncDTO, List<DatasourceEntity> list) {
        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity formDatasource = first1.get();
        DatasourceEntity toDatasource = first2.get();

        TableEntity fromTable = tableDao.findByTableNameAndSourceId(syncDTO.getFromTable(), formDatasource.getId());
        if (fromTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        List<FieldEntity> fieldList = fieldDao.findByTableId(fromTable.getId());

        SearchDTO searchDTO = new SearchDTO();

        searchDTO.setList(fieldList.stream().map(e->{
            FieldDTO fieldDTO = new FieldDTO();
            BeanUtils.copyProperties(e, fieldDTO);
            return fieldDTO;
        }).collect(Collectors.toList()));

        TableDTO fromDto = new TableDTO();
        BeanUtils.copyProperties(fromTable, fromDto);

        searchDTO.setTableEntity(fromDto);
        searchDTO.setTenantId(syncDTO.getTenantId());

        ResultVO<List<Row>> sync = remoteSyncInsideService.getSync(syncDTO.getFromTable(), searchDTO);

        if (!ResultVO.isSuccess(sync) || StringUtils.isEmpty(sync.getData())){
            return;
        }

        getAliasRow(fieldList, sync.getData());

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());

            QueryCondition empty = QueryCondition.createEmpty();
            Optional<FieldEntity> delFlag = toFieldList.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                empty.and(condition);
            } else {
                empty.and("1 = 1");
            }

            Optional<FieldEntity> first = toFieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getFromTenantId());
                empty.and(condition);
            }

            // 删除全量
            Db.deleteByCondition(toTable.getTableName(), empty);

            List<Row> rowList = getRows(toFieldList, sync.getData(), syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
            Db.insertBatch(toTable.getTableName(), rowList, Constants.ONE_THOUSAND * Constants.TEN);
        }finally{
            DataSourceKey.clear();
        }
    }

    @Override
    public void dealIncrementRpc(SyncDTO syncDTO, List<DatasourceEntity> list) {
        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity formDatasource = first1.get();
        DatasourceEntity toDatasource = first2.get();

        TableEntity fromTable = tableDao.findByTableNameAndSourceId(syncDTO.getFromTable(), formDatasource.getId());
        if (fromTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        List<FieldEntity> fieldEntities = fieldDao.findByTableId(fromTable.getId());
        if (StringUtils.isEmpty(fieldEntities)){
            throw new ServiceException(ResultEnum.BE_CURRENT, "表字段未配置完整");
        }
        List<FieldEntity> data = fieldEntities.stream().filter(FieldEntity::getDateKey).collect(Collectors.toList());
        if (StringUtils.isEmpty(data)){
            throw new ServiceException(ResultEnum.BE_CURRENT, "同步时间字段未配置完整");
        }

        SearchDTO dto = new SearchDTO();
        dto.setList(fieldEntities.stream().map(e->{
            FieldDTO fieldDTO = new FieldDTO();
            BeanUtils.copyProperties(e, fieldDTO);
            return fieldDTO;
        }).collect(Collectors.toList()));

        TableDTO fromDto = new TableDTO();
        BeanUtils.copyProperties(fromTable, fromDto);
        dto.setTableEntity(fromDto);
        dto.setTenantId(syncDTO.getFromTenantId());
        dto.setEndTime(syncDTO.getEndTime());
        dto.setBeginTime(syncDTO.getBeginTime());

        ResultVO<List<Row>> increase = remoteSyncInsideService.getSyncIncrease(fromTable.getTableName(), dto);

        if (ResultVO.isSuccess(increase) || StringUtils.isEmpty(increase.getData())){
            return;
        }

        List<Row> fromRows = increase.getData();

        // 设置别名
        getAliasRow(fieldEntities, fromRows);

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());

            Optional<FieldEntity> first = fieldEntities.stream().filter(FieldEntity::getIdKey).findFirst();
            if (first.isPresent()){
                FieldEntity entity = first.get();
                Object[] collect;
                if (entity.getAliasName() != null){
                    collect = fromRows.stream().map(e -> e.get(entity.getAliasName()).toString()).toArray();
                } else {
                    collect = fromRows.stream().map(e -> e.get(entity.getFieldName()) == null ? e.get(entity.getFieldName().toLowerCase()).toString() : e.get(entity.getFieldName()).toString()).toArray();
                }

                List<Row> delData = new ArrayList<>();
                QueryCondition query = null;

                QueryCondition condition;
                if (entity.getAliasName() != null){
                    condition = QueryCondition.create(new QueryColumn(entity.getAliasName()), SqlOperator.IN, collect);

                } else {
                    condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.IN, collect);
                }
                query = condition;
                delData = Db.selectListByCondition(toTable.getTableName(), query);

                Optional<FieldEntity> dataFlag = toFieldList.stream().filter(FieldEntity::getDataFlag).findFirst();
                if (dataFlag.isPresent()){
                    FieldEntity field = dataFlag.get();
                    query.and(QueryCondition.create(new QueryColumn(field.getFieldName()), 0));
                }

                Db.deleteByCondition(toTable.getTableName(), query);

                if (StringUtils.isNotEmpty(fromRows)){
                    if (dataFlag.isPresent()){
                        if (entity.getAliasName() != null){
                            List<String> collected = delData.stream().filter(e->Constants.ONE_STR.equals(e.get(dataFlag.get().getFieldName()).toString())).map(e -> e.get(entity.getAliasName()).toString()).collect(Collectors.toList());
                            fromRows.removeIf(e->collected.contains(e.get(entity.getAliasName()).toString()));
                        } else {
                            List<String> collected = delData.stream().filter(e->Constants.ONE_STR.equals(e.get(dataFlag.get().getFieldName()).toString())).map(e -> e.get(entity.getFieldName()).toString()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                            fromRows.removeIf(e->collected.contains(e.get(entity.getFieldName()).toString()));
                        }
                    }
                }
            }

            if (StringUtils.isNotEmpty(fromRows)){
                List<Row> rowList = getRows(toFieldList, fromRows, syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
                Db.insertBatch(toTable.getTableName(), rowList, Constants.TEN_THOUSAND * Constants.TEN);
            }
        }finally{
            DataSourceKey.clear();
        }
    }

    @Override
    public void syncScriptRpc(SyncScriptDTO syncDTO) {
        List<String> sourceIds = Arrays.asList(syncDTO.getFromTarget(), syncDTO.getToTarget());

        List<DatasourceEntity> list = datasourceDao.findByTargetIn(sourceIds);
        if (new HashSet<>(sourceIds).size() != list.size()){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        Optional<DatasourceEntity> first1 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getFromTarget())).findFirst();
        Optional<DatasourceEntity> first2 = list.stream().filter(e -> e.getTarget().equals(syncDTO.getToTarget())).findFirst();
        if (!first1.isPresent() || !first2.isPresent()) {
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        DatasourceEntity toDatasource = first2.get();


        TableEntity toTable = tableDao.findByTableNameAndSourceId(syncDTO.getToTable(), toDatasource.getId());
        if (toTable == null){
            throw new ServiceException(ResultEnum.ERROR, ErrorConstants.DATA_NOT_RIGHT);
        }

        ResultVO<List<Row>> sync = remoteSyncInsideService.getSyncByScript(syncDTO.getScript());

        if (!ResultVO.isSuccess(sync) || StringUtils.isEmpty(sync.getData())){
            return;
        }

        List<FieldEntity> toFieldList = fieldDao.findByTableId(toTable.getId());
        try {
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getToTarget());
            if (StringUtils.isEmpty(toFieldList)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "表字段未配置完整");
            }
            List<FieldEntity> collect = toFieldList.stream().filter(FieldEntity::getDateKey).collect(Collectors.toList());
            if (StringUtils.isEmpty(collect)){
                throw new ServiceException(ResultEnum.BE_CURRENT, "同步时间字段未配置完整");
            }

            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getFromTarget());
            QueryWrapper wrapper = QueryWrapper.create();

            Optional<FieldEntity> delFlag = toFieldList.stream().filter(e->{
                if(e.getDataDelFlag() != null){
                    return e.getDataDelFlag();
                }
                return false;
            }).findFirst();
            if (delFlag.isPresent()){
                FieldEntity entity = delFlag.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, entity.getDataDelFlagDefault());
                wrapper.and(condition);
            }

            Optional<FieldEntity> first = toFieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            if (first.isPresent() && syncDTO.getTenantId() != null){
                FieldEntity entity = first.get();
                QueryCondition condition = QueryCondition.create(new QueryColumn(entity.getFieldName()), SqlOperator.EQUALS, syncDTO.getTenantId());
                wrapper.and(condition);
            }

            QueryColumn column = new QueryColumn(collect.get(0).getFieldName());
            QueryCondition condition = column.between(syncDTO.getBeginTime(), syncDTO.getEndTime());
            for (int i = 1; i < collect.size(); i++) {
                condition = condition.or(new QueryColumn(collect.get(i).getFieldName()).between(syncDTO.getBeginTime(), syncDTO.getEndTime()));
            }
            wrapper.and(condition);

            Db.deleteByQuery(toTable.getTableName(), wrapper);
            List<Row> rowList = getRows(toFieldList, sync.getData(), syncDTO.getTenantId(), syncDTO.getDefaultAggregationParam());
            Db.insertBatch(toTable.getTableName(), rowList, Constants.TEN_THOUSAND * Constants.TEN);
        } finally {
            DataSourceKey.clear();
        }
    }

    @Override
    public List<Row> getSyncScript(SyncScriptDTO syncDTO) {

        DatasourceEntity target = datasourceDao.findByTarget(syncDTO.getFromTarget());
        if (target == null){
            throw new ServiceException(ResultEnum.BE_CURRENT, "服务异常！");
        }

        List<Row> fromRows;
        try{
            DataSourceKey.use(syncDTO.getTenantId()+"-"+syncDTO.getFromTarget());
            if (StringUtils.isNotEmpty(syncDTO.getScript())){
                syncDTO.setScript(StringEscapeUtils.unescapeHtml4(syncDTO.getScript()));
            }
            fromRows = Db.selectListBySql(syncDTO.getScript());
        }finally{
            DataSourceKey.clear();
        }

        return fromRows;
    }

    /**
     * 组装数据
     * @param fieldList
     * @param fromRows
     * @param tenantId
     * @return
     */

    public static List<Row> getRows(List<FieldEntity> fieldList, List<Row> fromRows, Long tenantId, List<String> defaultAggregationParam) {
        List<Row> rowList = new ArrayList<>();
        fromRows.forEach(e->{
            Row row = new Row();
            Set<String> dataKeys = e.keySet();
            dataKeys.forEach(ex -> {
                Optional<FieldEntity> any = fieldList.stream().filter(x -> x.getFieldName().equalsIgnoreCase(ex)).findAny();
                if (any.isPresent()){
                    row.set(ex.toLowerCase(), e.get(ex) == null ? e.get(ex.toLowerCase()) : e.get(ex));
                }
            });

            Optional<FieldEntity> lesseeFirst = fieldList.stream().filter(FieldEntity::getLesseeKey).findFirst();
            lesseeFirst.ifPresent(fieldEntity -> {
                String name = fieldEntity.getFieldName();
                row.set(name.toLowerCase(), tenantId);
            });

            Optional<FieldEntity> idFirst = fieldList.stream().filter(FieldEntity::getIdKey).findFirst();
            idFirst.ifPresent(fieldEntity -> {
                if (Constants.ONE.toString().equals(fieldEntity.getIdFormationStrategy())){
                    row.set(fieldEntity.getFieldName().toLowerCase(), ID_WORK_UTILS.nextId());
                }
                if (Constants.TWO.toString().equals(fieldEntity.getIdFormationStrategy())){
                    String name = fieldEntity.getIdKeyFrom();
                    row.set(fieldEntity.getFieldName().toLowerCase(), e.get(name) == null ? e.get(name.toLowerCase()) : e.get(name));
                }
            });

            if (StringUtils.isNotEmpty(defaultAggregationParam)){
                for (String param : defaultAggregationParam) {
                    Optional<FieldEntity> any = fieldList.stream().filter(x -> x.getFieldName().equalsIgnoreCase(param)).findAny();
                    any.ifPresent(fieldEntity -> {
                        if (StringUtils.isNotEmpty(fieldEntity.getFieldNameDefault())){
                            if ("current_time".equals(fieldEntity.getFieldNameDefault())){
                                row.set(param.toLowerCase(), DateUtils.format(new Date(), DateUtils.DATE_FORMAT_19));
                            } else {
                                row.set(param.toLowerCase(), fieldEntity.getFieldNameDefault());
                            }
                        }
                    });
                }
            }

            rowList.add(row);
        });
        return rowList;
    }


    public static void getAliasRow(List<FieldEntity> fieldList, List<Row> fromRows){
        List<FieldEntity> collect = fieldList.stream().filter(e -> StringUtils.isNotBlank(e.getAliasName())).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(collect)){
            for (Row row : fromRows) {
                for (FieldEntity entity : collect) {
                    if (StringUtils.isNotEmpty(entity.getAliasName())){
                        Object o = row.get(entity.getFieldName()) == null ? row.get(entity.getFieldName().toLowerCase()) : row.get(entity.getFieldName());
                        row.set(entity.getAliasName(), o);
                        row.remove(entity.getFieldName());
                    }
                }
            }
        }
    }
}

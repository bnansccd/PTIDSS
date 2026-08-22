package com.troy.form.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.mapper.DbTableMapper;
import com.troy.form.service.DbTableService;
import com.troy.form.dao.DatasourceDao;
import com.troy.form.dao.DbColumnDao;
import com.troy.form.dao.DbTableDao;
import com.troy.form.domain.DTO.DbTableSearchDTO;
import com.troy.form.domain.DTO.TableColumnDTO;
import com.troy.form.domain.DTO.TableDTO;
import com.troy.form.domain.VO.DbTableVO;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbColumnEntity;
import com.troy.form.module.sql.JdbcHelper;
import com.troy.form.module.sql.JdbcTranslate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DbTableServiceImpl extends ServiceImpl<DbTableMapper, DbTableEntity> implements DbTableService {

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DbTableDao dbTableDao;

    @Autowired
    private DbColumnDao dbColumnDao;

    @Autowired
    private DbColumnDao formDbColumnDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTable(TableDTO dto) {
        DatasourceEntity dao = datasourceDao.getById(dto.getDbId());
        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        if (isExist(dto.getTableName(), null, dao.getId())){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据库表");
        }

        for (TableColumnDTO column : dto.getList()) {
            if (JdbcHelper.judgeParameterRange(column.getSystemDataType(), column.getLength(), column.getScale())){
                throw new ServiceException(ResultEnum.ERROR, "数据类型与字段长度");
            }
        }

        DbTableEntity dbTableEntity = new DbTableEntity();
        BeanUtils.copyProperties(dto, dbTableEntity);
        dbTableDao.save(dbTableEntity);

        List<TableColumnDTO> list = dto.getList();
        List<DbColumnEntity> collect = list.stream().map(e -> {
            DbColumnEntity dbColumnEntity = new DbColumnEntity();
            BeanUtils.copyProperties(e, dbColumnEntity);
            dbColumnEntity.setTableId(dbTableEntity.getId());
            dbColumnEntity.setStatus(DictValueEnums.COLUMN_ADD.getCode());
            return dbColumnEntity;
        }).collect(Collectors.toList());

        dbColumnDao.saveBatch(collect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTable(Long tableId, TableDTO dto) {
        DatasourceEntity dao = datasourceDao.getById(dto.getDbId());
        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        if (isExist(dto.getTableName(), null, dao.getId())){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据库表");
        }

        for (TableColumnDTO column : dto.getList()) {
            if (JdbcHelper.judgeParameterRange(column.getSystemDataType(), column.getLength(), column.getScale())){
                throw new ServiceException(ResultEnum.ERROR, "数据类型与字段长度");
            }
        }

        List<DbColumnEntity> list = dbColumnDao.findByTableId(tableId);
        List<DbColumnEntity> delete = list.stream().filter(e -> {
            Optional<TableColumnDTO> first = dto.getList().stream().filter(x -> e.getId().equals(x.getId())).findFirst();
            return first.isPresent();
        }).collect(Collectors.toList());

        if (StringUtils.isNotEmpty(delete)){
            List<Long> collect = delete.stream().map(DbColumnEntity::getId).collect(Collectors.toList());
            dbColumnDao.removeByIds(collect);
        }


    }

    @Override
    public boolean isExist(String tableName, Long id, Long dbId) {
        DbTableEntity table = dbTableDao.findByTable(tableName, dbId);
        if (table == null){
            return false;
        }
        return !table.getId().equals(id);
    }

    @Override
    public PageVO<DbTableVO> getList(DbTableSearchDTO dto) {
        DatasourceEntity dao = datasourceDao.getById(dto.getDatasourceId());

        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        JdbcTranslate translate = JdbcHelper.getTranslate(dao);
        List<DbTableEntity> list = translate.getAllTable(dao);

        if (StringUtils.isNotBlank(dao.getName())){
            List<DbTableEntity> collect = list.stream().filter(e -> {
                if(StringUtils.isNotBlank(dto.getName())){
                    return e.getTableName().contains(dto.getName());
                } else {
                    return true;
                }
            }).collect(Collectors.toList());
            return PageUtils.pageVo(dto, collect);
        }
        return new PageVO<>();
    }

    @Override
    public void addTable(Long id, String tableName) {
        if (JdbcHelper.isKey(tableName)){
            throw new ServiceException(ResultEnum.ERROR, "表名称不规范");
        }

        DatasourceEntity dao = datasourceDao.getById(id);

        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        DbTableEntity table = dbTableDao.findByTable(tableName, id);
        if (table != null){
            throw new ServiceException(ResultEnum.EXIST, tableName);
        }

        JdbcTranslate translate = JdbcHelper.getTranslate(dao);
        DbTableEntity entity = translate.getCurrentTable(dao, tableName);
        if (entity == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, tableName);
        }
        entity.setUpdateStatus(Constants.TRUE);
        entity.setIsCreated(Constants.FALSE);
        entity.setDbId(id);
        dbTableDao.save(entity);

        List<DbColumnEntity> columns = translate.getDbColumns(dao, tableName);
        AtomicLong sort = new AtomicLong(1);
        columns.forEach(e-> {
            e.setTableId(entity.getId());
            e.setSort(sort.getAndIncrement());
        });
        formDbColumnDao.saveBatch(columns);
    }

    @Override
    public String getSQl(Long id) {
        DbTableEntity entity = dbTableDao.getById(id);
        if (entity == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据库表");
        }

        DatasourceEntity dao = datasourceDao.getById(entity.getDbId());
        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        List<DbColumnEntity> list = dbColumnDao.findByTableId(id);
        JdbcTranslate translate = JdbcHelper.getTranslate(dao);
        return translate.generateSql(list, entity);
    }
}

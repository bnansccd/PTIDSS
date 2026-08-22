package com.troy.form.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.form.entity.DbTableEntity;
import com.troy.form.service.DatasourceService;
import com.troy.form.dao.DatasourceDao;
import com.troy.form.dao.DbColumnDao;
import com.troy.form.dao.DbTableDao;
import com.troy.form.domain.DTO.DatasourceDTO;
import com.troy.form.domain.DTO.DatasourceSearchDTO;
import com.troy.form.domain.VO.DatasourceVO;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbColumnEntity;
import com.troy.form.module.sql.JdbcHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


/**
 * @author chenxl
 * @date 2023/6/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DbColumnDao formDbColumnDao;

    @Autowired
    private DbTableDao dbTableDao;

    @Override
    public void addDatasource(DatasourceDTO datasourceDTO) {
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        if (judgeDatasource(null, datasourceDTO)){
            DatasourceEntity datasourceEntity = new DatasourceEntity();
            BeanUtils.copyProperties(datasourceDTO, datasourceEntity);

            datasourceEntity.setType(JdbcHelper.getDbType(datasourceDTO.getUrl()));
            datasourceDao.save(datasourceEntity);

            JdbcHelper.judge(datasourceDTO);

            //新的数据源
            DruidDataSource newDataSource = new DruidDataSource();
            newDataSource.setUrl(datasourceDTO.getUrl());
            newDataSource.setPassword(datasourceDTO.getPassword());
            newDataSource.setUsername(datasourceDTO.getUsername());
            newDataSource.setDbType(datasourceEntity.getType());
            newDataSource.setDriverClassName(datasourceDTO.getDriver());
            flexDataSource.addDataSource(SecurityContextHolder.getTenantId()+"_"+ datasourceDTO.getIdentification(), newDataSource);

        } else {
            throw new ServiceException(ResultEnum.EXIST,  "Datasource标识");
        }
    }

    @Override
    public void deleteDatasource(Long id) {
        DatasourceEntity dao = datasourceDao.getById(id);
        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND,  "Datasource");
        }

        datasourceDao.removeById(id);

        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        flexDataSource.removeDatasource(SecurityContextHolder.getTenantId()+"_"+dao.getIdentification());
    }

    @Override
    public boolean judgeDatasource(Long id, DatasourceDTO datasourceDTO) {
        DatasourceEntity dao = datasourceDao.findByIdentification(datasourceDTO.getIdentification(), datasourceDTO.getName());
        if (dao == null){
            return true;
        }
        return dao.getId().equals(id);
    }

    @Override
    public boolean updateTable(Long id) {
        DatasourceEntity dao = datasourceDao.getById(id);

        if (dao == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        DbType dbType = flexDataSource.getDbType(SecurityContextHolder.getTenantId() + "_" + dao.getIdentification());

        if (dbType == null){
            DruidDataSource newDataSource = new DruidDataSource();
            newDataSource.setUrl(dao.getUrl());
            newDataSource.setPassword(dao.getPassword());
            newDataSource.setUsername(dao.getUsername());
            newDataSource.setDbType(dao.getType());
            newDataSource.setDriverClassName(dao.getDriver());
            flexDataSource.addDataSource(SecurityContextHolder.getTenantId() + "_" + dao.getIdentification(), newDataSource);
        }

        try{
            DataSourceKey.use(SecurityContextHolder.getTenantId() + "_" + dao.getIdentification());
            List<DbTableEntity> list = dbTableDao.findAllTable();
            dbTableDao.saveBatch(list);

            List<DbColumnEntity> entities = formDbColumnDao.findAll();
            entities.forEach(e->{
                Optional<DbTableEntity> first = list.stream().filter(x -> x.getTableName().equals(e.getTableSchema())).findFirst();
                first.ifPresent(sysDbTableEntity -> e.setTableId(sysDbTableEntity.getId()));
            });

            formDbColumnDao.saveBatch(entities);
        }finally{
            DataSourceKey.clear();
        }
        return false;
    }

    @Override
    public DatasourceVO getById(Serializable id) {
        DatasourceEntity dao = datasourceDao.getById(id);

        DatasourceVO vo = new DatasourceVO();
        BeanUtils.copyProperties(dao, vo);
        return vo;
    }

    @Override
    public PageVO<DatasourceVO> findPage(DatasourceSearchDTO dto) {
        Page<DatasourceEntity> page = datasourceDao.findPage(dto);
        if (StringUtils.isNotEmpty(page.getRecords())){
            page.getRecords().forEach(e->{
                e.setPassword("");
                e.setUrl("");
                e.setUsername("");
            });
        }
        return PageUtils.convertPageVo(page, DatasourceVO.class);
    }

}

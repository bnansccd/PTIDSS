package com.troy.form.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.form.mapper.DbSqlMapper;
import com.troy.form.service.DbSqlService;
import com.troy.form.dao.DatasourceDao;
import com.troy.form.dao.DbSqlDao;
import com.troy.form.domain.DTO.DbSqlDTO;
import com.troy.form.domain.DTO.DbSqlSearchDTO;
import com.troy.form.domain.VO.DbSqlVO;
import com.troy.form.entity.DatasourceEntity;
import com.troy.form.entity.DbSqlEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@Service
public class DbSqlServiceImpl extends ServiceImpl<DbSqlMapper, DbSqlEntity> implements DbSqlService {

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DbSqlDao dbSqlDao;

    @Override
    public void addSql(DbSqlDTO dbSqlDTO) {
        DatasourceEntity datasourceEntity = datasourceDao.getById(dbSqlDTO.getDbId());
        if (datasourceEntity == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        if (dbSqlDao.exists(null, dbSqlDTO.getName(), dbSqlDTO.getCode())){
            throw new ServiceException(ResultEnum.NOT_FOUND, "sql");
        }

        DbSqlEntity sqlEntity = new DbSqlEntity();
        BeanUtils.copyProperties(dbSqlDTO, sqlEntity);
        dbSqlDao.save(sqlEntity);
    }

    @Override
    public void updateSql(Long id, DbSqlDTO dbSqlDTO) {
        DatasourceEntity datasourceEntity = datasourceDao.getById(dbSqlDTO.getDbId());
        if (datasourceEntity == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "数据源");
        }

        if (dbSqlDao.exists(id, dbSqlDTO.getName(), dbSqlDTO.getCode())){
            throw new ServiceException(ResultEnum.NOT_FOUND, "sql");
        }

        DbSqlEntity sqlEntity = new DbSqlEntity();
        BeanUtils.copyProperties(dbSqlDTO, sqlEntity);
        sqlEntity.setId(id);
        dbSqlDao.updateById(sqlEntity);
    }

    @Override
    public PageVO<DbSqlVO> getPage(DbSqlSearchDTO searchDTO) {
        Page<DbSqlEntity> page = dbSqlDao.findPage(searchDTO);
        PageVO<DbSqlVO> vo = PageUtils.convertPageVo(page, DbSqlVO.class);
        if (StringUtils.isNotEmpty(vo.getRecords())){
            List<Long> list = vo.getRecords().stream().map(DbSqlVO::getDbId).collect(Collectors.toList());
            List<DatasourceEntity> entities = datasourceDao.listByIds(list);
            vo.getRecords().forEach(e->{
                entities.forEach(x->{
                    if (e.getDbId().equals(x.getId())){
                        e.setDbName(x.getName());
                    }
                });
            });
        }
        return vo;
    }
}

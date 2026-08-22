package com.troy.form.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.dao.DatasourceDao;
import com.troy.form.entity.DatasourceEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author chenxl
 * @date 2023/11/6
 */
@Configuration
public class MultiDatasourceConfig implements InitializingBean {

    @Autowired
    private DatasourceDao datasourceDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<DatasourceEntity> all = datasourceDao.findAll();
        FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        if (StringUtils.isNotEmpty(all)){
            all.forEach(sysDatasourceDTO->{
                //新的数据源
                DruidDataSource newDataSource = new DruidDataSource();
                newDataSource.setUrl(sysDatasourceDTO.getUrl());
                newDataSource.setPassword(sysDatasourceDTO.getPassword());
                newDataSource.setUsername(sysDatasourceDTO.getUsername());
                newDataSource.setDbType(sysDatasourceDTO.getType());
                newDataSource.setDriverClassName(sysDatasourceDTO.getDriver());
                flexDataSource.addDataSource(sysDatasourceDTO.getTenantId()+"_"+sysDatasourceDTO.getIdentification(), newDataSource);
            });
        }


    }

}

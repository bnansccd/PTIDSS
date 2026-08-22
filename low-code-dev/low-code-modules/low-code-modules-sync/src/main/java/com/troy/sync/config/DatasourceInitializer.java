package com.troy.sync.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.utils.StringUtils;
import com.troy.sync.dao.DatasourceDao;
import com.troy.sync.entity.DatasourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.mybatisflex.core.tenant.TenantManager.ignoreTenantCondition;
import static com.mybatisflex.core.tenant.TenantManager.restoreTenantCondition;

@Configuration
@Order
public class DatasourceInitializer {

    @Autowired
    private DatasourceDao datasourceDao;

    @PostConstruct
    public void init(){
        try {
            ignoreTenantCondition();

            List<DatasourceEntity> all = datasourceDao.findAll();
            FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();

            if (StringUtils.isNotEmpty(all)){
                all.forEach(e->{
                    if (Constants.ZERO.equals(e.getIsRpc())){
                        DruidDataSource newDataSource = new DruidDataSource();
                        newDataSource.setUrl(e.getUrl());
                        newDataSource.setPassword(e.getPassword());
                        newDataSource.setUsername(e.getUsername());
                        newDataSource.setDriverClassName(e.getDriver());
                        flexDataSource.addDataSource(e.getLessee()+"-"+e.getTarget(), newDataSource);
                    }
                });
            }

        } finally {
            restoreTenantCondition();
        }
    }

}

package com.troy.common.datasource.config;

import com.mybatisflex.core.tenant.TenantFactory;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/21 15:15:41
 * @Description: MyConfiguration
 * @Version: 1.0.0
 */
public class MyConfiguration {

    @Bean
    public TenantFactory tenantFactory() {
        TenantFactory tenantFactory = new TenantFactory() {
            @Override
            public Object[] getTenantIds() {
                Long tenantId = SecurityUtils.getTenantId();
                if (StringUtils.isNotNull(tenantId)) {
                    return new Object[]{tenantId};
                } else {
                    return null;
                }
            }
        };
        return tenantFactory;
    }
}

package com.troy.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxl
 * @date 2024/1/31
 */
@Configuration
@ConfigurationProperties(prefix = "sso")
@Data
public class SsoConfig {

    private String userUrl = "/esc-idm/api/v1/account/list";

    private String orgUrl = "/esc-idm/api/v1/org/list";

}

package com.troy.system.api.domain.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 基础数据
 * @Author: zhuQing
 * @Date: 2026/3/31 16:03
 * @Version: 1.0
 **/
@Data
public class BasicInfoVO implements Serializable {

    /**
     * 域名管理
     */
    private SysDomainNameVO sysDomainNameVO;

    /**
     * 租户管理
     */
    private SysTenantVO sysTenantVO;

    /**
     * 参数配置
     */
    private List<SysConfigVO> sysConfigVOS=new ArrayList<>();
}

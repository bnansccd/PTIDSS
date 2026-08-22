package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * @Description: 第三方用户体系的数据记录
 * @Author: zhuQing
 * @Date: 2025/6/7 09:40
 * @Version: 1.0
 **/
@Data
@Table("T_SYS_USER_THIRD_AUTH")
public class SysUserThirdAuthEntity extends TBaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 第三方用户唯一标识
     */
    private String openId;

    /**
     * 第三方平骀标识
     */
    private String loginType;


    /**
     * 第三方平骀标识
     */
    private String loginName;
}

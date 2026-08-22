package com.troy.system.entity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_config")
public class SysConfigEntity extends TBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    @Column("config_name")
    private String configName;

    /**
     * 参数键名
     */
    @Column("config_key")
    private String configKey;

    /**
     * 参数键值
     */
    @Column("config_value")
    private String configValue;

    /**
     * 备注
     */
    @Column("remarks")
    private String remarks;

    @Column("is_basic")
    private String isBasic;

}

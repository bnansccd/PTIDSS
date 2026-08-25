package com.ptidss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptidss.system.domain.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /** 幂等建表（与 DDL 17 双保险：旧库升级无需手工执行 DDL，应用启动即自动迁移） */
    @Update("CREATE TABLE IF NOT EXISTS sys_config ("
            + "id BIGINT PRIMARY KEY, "
            + "config_key VARCHAR(64) NOT NULL, "
            + "config_name VARCHAR(128) NOT NULL, "
            + "description VARCHAR(255), "
            + "config_group VARCHAR(32) NOT NULL, "
            + "config_type VARCHAR(16) NOT NULL DEFAULT 'string' "
            + "CHECK (config_type IN ('string','number','boolean','select','json')), "
            + "enum_values JSONB, "
            + "value JSONB NOT NULL, "
            + "is_sensitive BOOLEAN NOT NULL DEFAULT FALSE, "
            + "is_builtin BOOLEAN NOT NULL DEFAULT TRUE, "
            + "status VARCHAR(8) NOT NULL DEFAULT 'enabled' CHECK (status IN ('enabled','disabled')), "
            + "sort_order INT NOT NULL DEFAULT 100, "
            + "created_at TIMESTAMP NOT NULL DEFAULT now(), "
            + "updated_at TIMESTAMP NOT NULL DEFAULT now(), "
            + "version INT NOT NULL DEFAULT 1, "
            + "deleted BOOLEAN NOT NULL DEFAULT FALSE)")
    void createTableIfNotExists();

    /** 唯一索引（软删除感知） */
    @Update("CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_config_key ON sys_config(config_key) WHERE NOT deleted")
    void createUniqueIndex();
}

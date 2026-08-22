package com.ptidss.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ptidss.common.utils.SnowflakeIdGenerator;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 字段自动填充（数据字典通用约定：雪花 ID、created_at/updated_at/version/deleted 默认值）
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 雪花 ID 应用层生成：仅填充未显式赋值的主键（实体 @TableId(INPUT) 场景）
        if (metaObject.hasSetter("id") && metaObject.getValue("id") == null) {
            metaObject.setValue("id", SnowflakeIdGenerator.nextId());
        }
        Date now = new Date();
        this.strictInsertFill(metaObject, "createdAt", Date.class, now);
        this.strictInsertFill(metaObject, "updatedAt", Date.class, now);
        this.strictInsertFill(metaObject, "version", Integer.class, 1);
        this.strictInsertFill(metaObject, "deleted", Boolean.class, Boolean.FALSE);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", Date.class, new Date());
    }
}

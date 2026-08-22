package com.troy.system.entity;

import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.TBaseEntity;
import lombok.Data;

/**
 * @Author Chenxl
 * @Date 2024/5/20
 */
@Data
@Table("t_sys_message_template")
public class MessageTemplateEntity extends TBaseEntity {

    private String details;

    private String name;

    private String msgMark;

}

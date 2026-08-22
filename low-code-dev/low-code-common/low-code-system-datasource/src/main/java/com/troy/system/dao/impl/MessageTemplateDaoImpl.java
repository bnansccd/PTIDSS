package com.troy.system.dao.impl;


import com.troy.common.datasource.service.impl.BaseServiceImpl;
import com.troy.system.dao.MessageTemplateDao;
import com.troy.system.entity.MessageTemplateEntity;
import com.troy.system.entity.table.MessageTemplateEntityTableDef;
import com.troy.system.mapper.MessageTemplateMapper;
import org.springframework.stereotype.Component;

/**
 * @author sym
 * @since 2024/5/20 14:13
 */
@Component
public class MessageTemplateDaoImpl extends BaseServiceImpl<MessageTemplateMapper, MessageTemplateEntity> implements MessageTemplateDao {

    @Override
    public MessageTemplateEntity findByMsgMark(String mark) {
        return getOne(query().where(MessageTemplateEntityTableDef.MESSAGE_TEMPLATE_ENTITY.MSG_MARK.eq(mark)));
    }

}

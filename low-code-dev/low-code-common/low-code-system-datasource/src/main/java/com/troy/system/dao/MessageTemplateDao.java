package com.troy.system.dao;

import com.troy.common.datasource.service.BaseService;
import com.troy.system.entity.MessageTemplateEntity;

public interface MessageTemplateDao extends BaseService<MessageTemplateEntity> {

    MessageTemplateEntity findByMsgMark(String mark);

}

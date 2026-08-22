package com.troy.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.troy.system.dao.MessageLogDao;
import com.troy.system.dao.MessageTemplateDao;
import com.troy.system.domain.DTO.MessageDTO;
import com.troy.system.domain.VO.MessageTemplateVO;
import com.troy.system.entity.MessageLogEntity;
import com.troy.system.entity.MessageTemplateEntity;
import com.troy.system.service.MessageService;
import com.troy.system.util.MessageSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private MessageLogDao messageLogDao;

    public String sendMessage(MessageDTO messageDTO) {
        MessageLogEntity messageLog = new MessageLogEntity();
        messageLog.setDetails(JSON.toJSONString(messageDTO));

        String string = MessageSendUtil.sendMessage(messageDTO.getMessage(), messageDTO.getPhones());
        messageLog.setReturnResult(string);
        messageLogDao.save(messageLog);
        return string;
    }

    @Override
    public List<MessageTemplateVO> findTemplate() {
        List<MessageTemplateEntity> list = messageTemplateDao.list();
        return list.stream().map(e->{
            MessageTemplateVO vo = new MessageTemplateVO();
            vo.setName(e.getName());
            vo.setDetails(e.getDetails());
            vo.setId(e.getId());
            return vo;
        }).collect(Collectors.toList());
    }


}

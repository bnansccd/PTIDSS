package com.troy.system.service;

import com.troy.system.domain.DTO.MessageDTO;
import com.troy.system.domain.VO.MessageTemplateVO;

import java.util.List;

public interface MessageService {


    String sendMessage(MessageDTO messageDTO);


    /**
     * 获取
     * @return
     */
    List<MessageTemplateVO> findTemplate();


}

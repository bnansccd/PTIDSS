package com.troy.system.service;

import com.troy.system.api.domain.DTO.OverrunMsgDTO;

import java.util.List;

public interface SendService {

    /**
     * 获取
     *
     * @param phoneNum
     */
    void sendMsg(String phoneNum, Long key);

    /**
     * 发送一超四罚短信
     *
     * @param dtos
     */
    void sendOverrunMsg(List<OverrunMsgDTO> dtos);

    /**
     * 发送常规短信
     *
     * @param phoneNums 手机号
     * @param msgMark  模板标识
     * @param params   参数
     */
    void sendNormalMsg(List<String> phoneNums, String msgMark, String... params);


    void sendNjMsg(String phoneNum, Long key);

}

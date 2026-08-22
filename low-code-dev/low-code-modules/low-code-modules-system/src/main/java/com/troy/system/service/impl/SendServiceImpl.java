package com.troy.system.service.impl;

import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.domain.DTO.OverrunMsgDTO;
import com.troy.system.dao.MessageLogDao;
import com.troy.system.dao.MessageTemplateDao;
import com.troy.system.dao.SysDepartDao;
import com.troy.system.dao.SysUserDao;
import com.troy.system.entity.MessageLogEntity;
import com.troy.system.entity.MessageTemplateEntity;
import com.troy.system.entity.SysDepartEntity;
import com.troy.system.entity.SysUserEntity;
import com.troy.system.service.SendService;
import com.troy.system.util.MessageSendUtil;
import com.troy.system.util.NjMASClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.troy.common.core.enums.ResultEnum.MSG_NOT_OUT_TIME;
import static com.troy.common.redis.constants.BaseRedisConstants.MSG_TIME;

@Service
@Slf4j
public class SendServiceImpl implements SendService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private MessageLogDao messageLogDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysDepartDao sysDepartDao;

    @Autowired
    private NjMASClient njMASClient;

    @Override
    public void sendMsg(String phoneNum, Long key) {
        log.info("tenantId:{},phone:{}", SecurityContextHolder.getTenantId(), phoneNum);
        if (key == 411353956455104512L){
            sendNjMsg(phoneNum, key);
        } else {
            MessageTemplateEntity msgMark = messageTemplateDao.findByMsgMark("MSG");
            if (msgMark == null) {
                throw new ServiceException(ResultEnum.BE_CURRENT, "消息模板不存在");
            }

            String details = msgMark.getDetails();

            Random random = new Random();
            int sixDigitNumber = random.nextInt(900000) + 100000;
            Boolean aBoolean = redisService.hasKey(MSG_TIME + key + ":" + phoneNum);
            if (aBoolean) {
                throw new ServiceException(MSG_NOT_OUT_TIME);
            }

            redisService.setCacheObject(MSG_TIME + key + ":" + phoneNum, String.valueOf(sixDigitNumber), 5L, TimeUnit.MINUTES);

            String result = details.replace("{}", String.valueOf(sixDigitNumber));
            String sent = MessageSendUtil.sendMessage(result, Collections.singletonList(phoneNum));

            MessageLogEntity messageLog = new MessageLogEntity();
            messageLog.setDetails(msgMark.getDetails() + phoneNum);

            messageLog.setReturnResult(sent);
            messageLogDao.save(messageLog);
        }
    }


    @Override
    public void sendNjMsg(String phoneNum, Long key) {
        String tmpId = "b94d46d5748b42e492e086d0e0964f81";

        Random random = new Random();
        int sixDigitNumber = random.nextInt(900000) + 100000;
        Boolean aBoolean = redisService.hasKey(MSG_TIME + key + ":" + phoneNum);
        if (aBoolean) {
            throw new ServiceException(MSG_NOT_OUT_TIME);
        }

        MessageLogEntity messageLog = new MessageLogEntity();
        messageLog.setDetails(tmpId + phoneNum);
        redisService.setCacheObject(MSG_TIME + key + ":" + phoneNum, String.valueOf(sixDigitNumber), 5L, TimeUnit.MINUTES);
        try {
            NjMASClient.JSONObject jsonObject = new NjMASClient.JSONObject();
            String sms = njMASClient.sendSmsWithParams(tmpId, phoneNum, Arrays.asList(String.valueOf(sixDigitNumber)), null);
            messageLog.setReturnResult(sms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        messageLogDao.save(messageLog);
    }


    @Override
    public void sendOverrunMsg(List<OverrunMsgDTO> dtos) {
        //移除没有部门 id 的数据
        dtos.removeIf(e->e.getDeptId() == null);

        List<Long> userIds = dtos.stream().map(e -> e.getUserId()).filter(StringUtils::isNotNull).distinct().collect(Collectors.toList());

        //通过用户 id 查询手机号来发短信
        if (StringUtils.isNotEmpty(userIds)) {
            List<SysUserEntity> sysUserEntities = sysUserDao.listByIds(userIds);
            for (SysUserEntity sysUserEntity : sysUserEntities) {
                dtos.stream().filter(e->Objects.equals(e.getUserId(),sysUserEntity.getId()))
                        .findFirst()
                        .ifPresent(e->e.setPhone(sysUserEntity.getPhone()));
            }
        }

        //移除未查询到手机号的数据
        dtos.removeIf(e->StringUtils.isBlank(e.getPhone()));

        //填充部门名称
        List<Long> deptIds = dtos.stream().map(e -> e.getDeptId()).filter(StringUtils::isNotNull).distinct().collect(Collectors.toList());
        if (StringUtils.isNotEmpty(deptIds)) {
            List<SysDepartEntity> sysDepartEntities = sysDepartDao.listByIds(deptIds);
            for (OverrunMsgDTO dto : dtos) {
                sysDepartEntities.stream().filter(e->Objects.equals(e.getId(),dto.getDeptId()))
                        .findFirst()
                        .ifPresent(e->dto.setDeptName(e.getDepartName()));
            }
        }

        Map<String, List<OverrunMsgDTO>> map = dtos.stream()
                .filter(e -> StringUtils.isNotBlank(e.getMsgMark()))
                .collect(Collectors.groupingBy(e -> e.getMsgMark()));

        for (String msgMark : map.keySet()) {
            if (DictValueEnums.OVERRUN_MSG_TYPE_WARNING.getCode().equalsIgnoreCase(msgMark)){
                this.sendOverrunWarningMsg(map.get(msgMark));
            } else if (DictValueEnums.OVERRUN_MSG_TYPE_OVERDUE.getCode().equalsIgnoreCase(msgMark)) {
                this.sendOverrunOverdueMsg(map.get(msgMark));
            }else if (DictValueEnums.OVERRUN_MSG_TYPE_DELIVER.getCode().equalsIgnoreCase(msgMark)) {
                this.sendOverrunDeliverMsg(map.get(msgMark));
            }

        }


    }

    @Override
    public void sendNormalMsg(List<String> phoneNums, String msgMark, String... params) {
        MessageTemplateEntity entity = messageTemplateDao.findByMsgMark(msgMark);
        if (StringUtils.isNotNull(entity)){
            String result = MessageFormat.format(entity.getDetails(), params);
            List<MessageLogEntity> logs = new ArrayList<>();
            for (String phoneNum : phoneNums) {
                String sent = MessageSendUtil.sendMessage(result, Collections.singletonList(phoneNum));

                MessageLogEntity messageLog = new MessageLogEntity();
                messageLog.setDetails(result + phoneNum);

                messageLog.setReturnResult(sent);
                logs.add(messageLog);
            }

            messageLogDao.saveBatch(logs);
        }
    }



    private void sendOverrunDeliverMsg(List<OverrunMsgDTO> list) {
        MessageTemplateEntity msgMark = messageTemplateDao.findByMsgMark(DictValueEnums.OVERRUN_MSG_TYPE_DELIVER.getCode());
        if (msgMark == null) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "消息模板不存在");
        }

        String details = msgMark.getDetails();
        List<MessageLogEntity> logs = new ArrayList<>();
        for (OverrunMsgDTO dto : list) {
            String result = MessageFormat.format(details, dto.getDeptName(), dto.getCaseNumber());
            String sent = MessageSendUtil.sendMessage(result, Collections.singletonList(dto.getPhone()));

            MessageLogEntity messageLog = new MessageLogEntity();
            messageLog.setDetails(result + dto.getPhone());

            messageLog.setReturnResult(sent);
            logs.add(messageLog);
        }
        messageLogDao.saveBatch(logs);

    }

    private void sendOverrunOverdueMsg(List<OverrunMsgDTO> list) {
        MessageTemplateEntity msgMark = messageTemplateDao.findByMsgMark(DictValueEnums.OVERRUN_MSG_TYPE_OVERDUE.getCode());
        if (msgMark == null) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "消息模板不存在");
        }

        String details = msgMark.getDetails();
        List<MessageLogEntity> logs = new ArrayList<>();
        for (OverrunMsgDTO dto : list) {
            String result = MessageFormat.format(details, dto.getDeptName(), dto.getCaseNumber());
            String sent = MessageSendUtil.sendMessage(result, Collections.singletonList(dto.getPhone()));

            MessageLogEntity messageLog = new MessageLogEntity();
            messageLog.setDetails(result + dto.getPhone());

            messageLog.setReturnResult(sent);
            logs.add(messageLog);
        }
        messageLogDao.saveBatch(logs);
    }

    private void sendOverrunWarningMsg(List<OverrunMsgDTO> list) {
        MessageTemplateEntity msgMark = messageTemplateDao.findByMsgMark(DictValueEnums.OVERRUN_MSG_TYPE_WARNING.getCode());
        if (msgMark == null) {
            throw new ServiceException(ResultEnum.BE_CURRENT, "消息模板不存在");
        }

        String details = msgMark.getDetails();
        List<MessageLogEntity> logs = new ArrayList<>();
        for (OverrunMsgDTO dto : list) {
            String result = MessageFormat.format(details, dto.getDeptName(), dto.getCaseNumber(),dto.getDayCount());
            String sent = MessageSendUtil.sendMessage(result, Collections.singletonList(dto.getPhone()));

            MessageLogEntity messageLog = new MessageLogEntity();
            messageLog.setDetails(result + dto.getPhone());

            messageLog.setReturnResult(sent);
            logs.add(messageLog);
        }
        messageLogDao.saveBatch(logs);
    }

}

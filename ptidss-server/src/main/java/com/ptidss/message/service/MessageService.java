package com.ptidss.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.message.domain.MessageRecord;
import com.ptidss.message.mapper.MessageRecordMapper;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * 我的消息（对齐 OpenAPI V1.1 /message/**；平台服务：分类/未读筛选/标记已读）
 * 消息为个人维度（receiver_id=当前登录用户），无 region 隔离诉求（DDL 10.5 无 region 列）
 */
@Service
public class MessageService {

    private final MessageRecordMapper messageRecordMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public MessageService(MessageRecordMapper messageRecordMapper,
                          SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.messageRecordMapper = messageRecordMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    /** 当前用户无消息时写入种子（幂等，5 类消息各 1 条 + 1 条已读示例） */
    private void ensureMessages(Long receiverId) {
        Long count = messageRecordMapper.selectCount(new LambdaQueryWrapper<MessageRecord>()
                .eq(MessageRecord::getReceiverId, receiverId));
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"forecast_summary", "次日负荷/电价预测已生成", "明日最大负荷预计 8520 万千瓦，"
                        + "日前现货均价预测 428 元/兆瓦时，请查阅预测看板。"},
                {"market_alert", "现货价格异动提醒", "近日前均价 428 元/兆瓦时，较昨日上涨 12%，"
                        + "峰段价差扩大，请关注申报策略。"},
                {"decision_todo", "待确认交易策略", "今日日滚动方案已生成，请在 16:00 前完成"
                        + "人机确认，逾期将按默认策略执行。"},
                {"settlement_diff", "结算差异工单已生成", "本月结算核对发现电费差异 1.2 万元，"
                        + "差异工单已派发，请及时处理。"},
                {"assess_reminder", "考核结果已发布", "本月交易考核结果已发布，偏差率 4.2% 在"
                        + "合格区间内，详细得分请查看考核报告。"},
        };
        for (int i = 0; i < seeds.length; i++) {
            MessageRecord msg = new MessageRecord();
            msg.setMsgType(seeds[i][0]);
            msg.setReceiverId(receiverId);
            msg.setTitle(seeds[i][1]);
            msg.setContent(seeds[i][2]);
            msg.setChannel(toJson(Collections.singletonList("web")));
            msg.setReadStatus(i == 2 ? "read" : "unread");
            msg.setBizRef("MSG-" + seeds[i][0].toUpperCase() + "-20260820");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -(i + 1));
            messageRecordMapper.insert(msg);
        }
    }

    /** 我的消息（分类/未读筛选，分页；个人维度） */
    public Result<Page<MessageRecord>> list(String msgType, Boolean unreadOnly,
                                            long pageNo, long pageSize) {
        Long receiverId = securityUtils.getUserId();
        ensureMessages(receiverId);
        LambdaQueryWrapper<MessageRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(MessageRecord::getReceiverId, receiverId)
                .eq(StrUtils.isNotBlank(msgType), MessageRecord::getMsgType, msgType)
                .eq(Boolean.TRUE.equals(unreadOnly), MessageRecord::getReadStatus, "unread")
                .orderByDesc(MessageRecord::getCreatedAt);
        return Result.success(messageRecordMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    /** 标记已读（校验消息归属当前用户，幂等） */
    public void markRead(Long id) {
        MessageRecord msg = messageRecordMapper.selectById(id);
        if (msg == null) {
            throw new ServiceException("消息不存在");
        }
        if (!msg.getReceiverId().equals(securityUtils.getUserId())) {
            throw new ServiceException("无权操作他人消息");
        }
        if (!"read".equals(msg.getReadStatus())) {
            msg.setReadStatus("read");
            messageRecordMapper.updateById(msg);
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败");
        }
    }
}

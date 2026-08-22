package com.ptidss.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.config.JsonStringTypeHandler;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的消息（DDL 10.5 message_record；个人维度，msg_type 分类/未读标记）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message_record")
public class MessageRecord extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 消息类型：forecast_summary/market_alert/decision_todo/settlement_diff/assess_reminder */
    private String msgType;

    /** 接收人 */
    private Long receiverId;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 送达渠道：web/miniapp */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String channel;

    /** 已读状态：unread/read */
    private String readStatus;

    /** 业务关联标识 */
    private String bizRef;
}

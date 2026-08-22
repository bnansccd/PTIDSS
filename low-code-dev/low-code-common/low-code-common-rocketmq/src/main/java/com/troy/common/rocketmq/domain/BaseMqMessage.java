package com.troy.common.rocketmq.domain;

import com.troy.common.core.utils.uuid.UUID;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/3 15:15:59
 * @Description: 基础消息实体，包含基础的消息
 * @Version: 1.0.0
 */
@Data
public abstract class BaseMqMessage {

    /**
     * 业务键，用于RocketMQ控制台查看消费情况
     */
    private String key;

    /**
     * 发送消息来源，用于排查问题
     */
    private String source;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime = LocalDateTime.now();

    /**
     * 跟踪id，用于slf4j等日志记录跟踪id，方便查询业务链
     */
    private String traceId = UUID.randomUUID().toString();

    /**
     * 重试次数，用于判断重试次数，超过重试次数发送异常警告
     */
    private Integer retryTimes = 0;
}

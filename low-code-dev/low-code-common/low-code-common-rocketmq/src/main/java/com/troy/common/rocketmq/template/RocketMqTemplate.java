package com.troy.common.rocketmq.template;

import com.alibaba.fastjson2.JSON;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.rocketmq.constant.RocketMqSysConstant;
import com.troy.common.rocketmq.domain.BaseMqMessage;
import com.troy.common.rocketmq.iterator.ListSplitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/3 16:16:06
 * @Description: RocketMQ模板类
 * @Version: 1.0.0
 */
@Slf4j
@Component
public class RocketMqTemplate {

    @Resource(name = "rocketMQTemplate")
    private RocketMQTemplate template;

    /**
     * 获取模板
     *
     * @return
     */
    public RocketMQTemplate getTemplate() {
        return template;
    }

    /**
     * 构建目的地
     *
     * @param topic
     * @param tag
     * @return
     */
    public String buildDestination(String topic, String tag) {
        return topic + RocketMqSysConstant.DELIMITER + tag;
    }

    /**
     * 同步消息：
     * 同步发送是指消息发送方发出数据后，会在收到接收方发回响应之后才会发送下一个数据包的通讯方式。
     * 此种方式应用场景非常广泛，例如重要通知邮件、报名短信通知、营销短信系统等。
     */
    public <T extends BaseMqMessage> SendResult send(String topic, String tag, T message) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        return send(destination, message);
    }

    public <T extends BaseMqMessage> SendResult send(String destination, T message) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        SendResult sendResult = template.syncSend(destination, sendMessage);
        // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
//        log.info("[{}]同步消息[{}]发送结果[{}]", destination, JSON.toJSONString(message), JSON.toJSONString(sendResult));
        return sendResult;
    }

    /**
     * 发送异步消息
     * 异步发送是指发送方发出数据后，不等接收方发回响应，接着发送下一个数据包的通讯方式。发送方通过回调接口接收服务器响应，并对响应结果进行处理。
     */
    public <T extends BaseMqMessage> void send(String topic, String tag, T message, SendCallback sendCallback) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        // 注意分隔符
        send(destination, message, sendCallback);
    }

    public <T extends BaseMqMessage> void send(String destination, T message, SendCallback sendCallback) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        template.asyncSend(destination, sendMessage, sendCallback);
        // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
        log.info("异步消息发送");
    }

    /**
     * 单向消息
     * 单向发送是指发送方只负责发送消息，不等待服务器回应，且没有回调函数触发。即只发送请求而不管响应。
     */
    public <T extends BaseMqMessage> void sendOneWay(String topic, String tag, T message) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        sendOneWay(destination, message);
    }

    public <T extends BaseMqMessage> void sendOneWay(String destination, T message) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        template.sendOneWay(destination, sendMessage);
        // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
        log.info("发送单向消息");
    }

    /**
     * 发送同步顺序消息
     */
    public <T extends BaseMqMessage> SendResult syncSendOrderly(String topic, String tag, T message, MessageQueueSelector messageQueueSelector) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        return syncSendOrderly(destination, message, messageQueueSelector);
    }

    public <T extends BaseMqMessage> SendResult syncSendOrderly(String destination, T message, MessageQueueSelector messageQueueSelector) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        if (StringUtils.isNotNull(messageQueueSelector)){
            template.setMessageQueueSelector(messageQueueSelector);
        }
        SendResult sendResult = template.syncSendOrderly(destination, sendMessage, message.getKey());
        // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
        if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
            log.info("发送同步顺序消息成功!");
        } else {
            log.error("发送同步顺序消息失败!消息ID为:{}", sendResult.getMsgId());
        }
        return sendResult;
    }

    /**
     * 发送异步顺序消息
     */
    public <T extends BaseMqMessage> void asyncSendOrderly(String topic, String tag, T message, MessageQueueSelector messageQueueSelector, SendCallback sendCallback) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        asyncSendOrderly(destination, message, messageQueueSelector, sendCallback);
    }

    public <T extends BaseMqMessage> void asyncSendOrderly(String destination, T message, MessageQueueSelector messageQueueSelector, SendCallback sendCallback) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        if (StringUtils.isNotNull(messageQueueSelector)){
            template.setMessageQueueSelector(messageQueueSelector);
        }
        template.asyncSendOrderly(destination, sendMessage, message.getKey(), sendCallback);
    }

    /**
     * 发送单向顺序消息
     */
    public <T extends BaseMqMessage> void sendOneWayOrderly(String topic, String tag, T message, MessageQueueSelector messageQueueSelector) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        sendOneWayOrderly(destination, message, messageQueueSelector);
    }

    public <T extends BaseMqMessage> void sendOneWayOrderly(String destination, T message, MessageQueueSelector messageQueueSelector) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        if (StringUtils.isNotNull(messageQueueSelector)){
            template.setMessageQueueSelector(messageQueueSelector);
        }
        template.sendOneWayOrderly(destination, sendMessage, message.getKey());
    }

    /**
     * 发送事务消息 定义事务消息需要自己定义监听器，实现 RocketMQLocalTransactionListener
     */
    public <T extends BaseMqMessage> TransactionSendResult sendMessageInTransaction(String topic, String tag, T message) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        return sendMessageInTransaction(destination, message);
    }

    public <T extends BaseMqMessage> TransactionSendResult sendMessageInTransaction(String destination, T message) {
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        TransactionSendResult result = template.sendMessageInTransaction(destination, sendMessage, message.getKey());
        if (result.getSendStatus() == SendStatus.SEND_OK) {
            log.info("发送事务消息成功!消息ID为:{}", result.getMsgId());
        }
        return result;
    }

    /**
     * 发送批量消息
     */
    public <T extends BaseMqMessage> List<SendResult> sendBatchMessage(String topic, String tag, List<BaseMqMessage> messages) {
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            // 注意分隔符
            destination = topic + RocketMqSysConstant.DELIMITER + tag;
        }
        return sendMessageInTransaction(destination, messages);
    }

    public <T extends BaseMqMessage> List<SendResult> sendMessageInTransaction(String destination, List<BaseMqMessage> messages) {
        List<SendResult> sendResults = new ArrayList<>();
        //限制数据大小
        ListSplitter splitter = new ListSplitter(1024 * 1024 * 1, messages);
        List<BaseMqMessage> next = null;
        SendResult result = null;
        while (splitter.hasNext()) {
            next = splitter.next();
            result = template.syncSend(destination, next);
            if (result.getSendStatus() == SendStatus.SEND_OK) {
                log.info("发送批量消息成功!消息ID为:{}", result.getMsgId());
            } else {
                log.info("发送批量消息失败!消息ID为:{},消息状态为:{}", result.getMsgId(), result.getSendStatus());
            }
            sendResults.add(result);
        }
        return sendResults;
    }


    /**
     * 发送延迟消息
     *
     * @param topic
     * @param tag
     * @param message
     * @param delayLevel
     * @param <T>
     * @return
     */
    public <T extends BaseMqMessage> SendResult send(String topic, String tag, T message, int delayLevel) {
        return send(topic + RocketMqSysConstant.DELIMITER + tag, message, delayLevel);
    }

    public <T extends BaseMqMessage> SendResult send(String destination, T message, int delayLevel) {
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        SendResult sendResult = template.syncSend(destination, sendMessage, 3000, delayLevel);
        log.info("[{}]延迟等级[{}]消息[{}]发送结果[{}]", destination, delayLevel, JSON.toJSONString(message), JSON.toJSONString(sendResult));
        return sendResult;
    }
}

package com.troy.common.rocketmq.listener;

import com.alibaba.fastjson2.JSON;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.rocketmq.template.RocketMqTemplate;
import com.troy.common.rocketmq.constant.RocketMqSysConstant;
import com.troy.common.rocketmq.domain.BaseMqMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.MDC;

import jakarta.annotation.Resource;
import java.time.Instant;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/3 16:16:36
 * @Description: 抽象消息监听器，封装了所有公共处理业务，如
 * 1、基础日志记录
 * 2、异常处理
 * 3、消息重试
 * 4、警告通知
 * 5、....
 * @Version: 1.0.0
 */
@Slf4j
public abstract class BaseMqMessageListener<T extends BaseMqMessage> {

    @Resource
    private RocketMqTemplate rocketMqTemplate;

    /**
     * 消息者名称
     *
     * @return 消费者名称
     */
    protected abstract String consumerName();

    /**
     * 消息处理
     *
     * @param message 待处理消息
     * @throws Exception 消费异常
     */
    protected abstract void handleMessage(T message) throws Exception;

    /**
     * 超过重试次数消息，需要启用isRetry
     *
     * @param message 待处理消息
     */
    protected abstract void overMaxRetryTimesMessage(T message);

    /**
     * 是否过滤消息，例如某些
     *
     * @param message 待处理消息
     * @return true: 本次消息被过滤，false：不过滤
     */
    protected boolean isFilter(T message) {
        return false;
    }

    /**
     * 是否异常时重复发送
     *
     * @return true: 消息重试，false：不重试
     */
    protected abstract boolean isRetry();

    /**
     * 消费异常时是否抛出异常
     *
     * @return true: 抛出异常，false：消费异常(如果没有开启重试则消息会被自动ack)
     */
    protected abstract boolean isThrowException();

    /**
     * 最大重试此处
     *
     * @return 最大重试次数，默认10次
     */
    protected int maxRetryTimes() {
        return 10;
    }

    /**
     * isRetry开启时，重新入队延迟时间
     *
     * @return -1：立即入队重试
     */
    protected int retryDelayLevel() {
        return -1;
    }

    /**
     * 由父类来完成基础的日志和调配
     */
    public void dispatchMessage(T message) {
        MDC.put(RocketMqSysConstant.TRACE_ID, message.getTraceId());
        // 基础日志记录被父类处理了
        log.info("[{}]消费者收到消息[{}]", consumerName(), JSON.toJSONString(message));
        if (isFilter(message)) {
            log.info("消息id{}不满足消费条件，已过滤。", message.getKey());
            return;
        }
        // 超过最大重试次数时调用子类方法处理
        if (message.getRetryTimes() > maxRetryTimes()) {
            overMaxRetryTimesMessage(message);
            return;
        }
        try {
            long start = Instant.now().toEpochMilli();
            handleMessage(message);
            long end = Instant.now().toEpochMilli();
            log.info("消息消费成功，耗时[{}ms]", (end - start));
        } catch (Exception e) {
            log.error("消息消费异常", e);
            // 是捕获异常还是抛出，由子类决定
            if (isThrowException()) {
                throw new RuntimeException(e);
            }
            if (isRetry()) {
                // 获取子类RocketMQMessageListener注解拿到topic和tag
                handleRetry(message);
            }
        }
    }

    protected void handleRetry(T message) {
        RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
        if (StringUtils.isNull(annotation)) {
            return;
        }
        message.setSource(message.getSource() + "消息重试");
        message.setRetryTimes(message.getRetryTimes() + 1);
        SendResult sendResult;
        try {
            // 如果消息发送不成功，则再次重新发送，如果发送异常则抛出由MQ再次处理(异常时不走延迟消息)
            // 此处捕获之后，相当于此条消息被消息完成然后重新发送新的消息
            sendResult = rocketMqTemplate.send(annotation.topic(), annotation.selectorExpression(), message, retryDelayLevel());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        // 发送失败的处理就是不进行ACK，由RocketMQ重试
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            throw new RuntimeException("重试消息发送失败");
        }
    }
}

package com.troy.common.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送简单消息
     */
    public void sendMessage(String myTopic, String message) {
        kafkaTemplate.send(myTopic, message);
    }

    /**
     * 发送带键的消息
     */
    public void sendMessageWithKey(String myTopic, String key, String message) {
        log.info("发送带键的消息: key={}, value={}", key, message);
        kafkaTemplate.send(myTopic, key, message);
    }


}
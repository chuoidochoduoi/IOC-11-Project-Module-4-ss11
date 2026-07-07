package org.example.medicinecachedemo.service;

import org.example.medicinecachedemo.model.AlertMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RedisMessagePublisher {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CHANNEL_NAME = "pharmacy-alerts";


    public void publish(AlertMessage alert) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(alert);
            redisTemplate.convertAndSend(CHANNEL_NAME, jsonMessage);
            log.info("=== ĐÃ GỬI MESSAGE ĐẾN CHANNEL {}: {} ===", CHANNEL_NAME, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Lỗi serialize message: {}", e.getMessage());
        }
    }

    public void publishImport(String message) {
        publish(AlertMessage.of("IMPORT", message));
    }
}
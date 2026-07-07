package org.example.medicinecachedemo.service;

import org.example.medicinecachedemo.model.AlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String jsonMessage = new String(message.getBody());
            AlertMessage alert = objectMapper.readValue(jsonMessage, AlertMessage.class);

            System.out.println("\n========================================");
            System.out.println(">>> NHẬN ALERT TỪ REDIS CHANNEL <<<");
            System.out.println("Type: " + alert.getType());
            System.out.println("Message: " + alert.getMessage());
            System.out.println("Time: " + alert.getTimestamp());
            System.out.println("========================================\n");

            log.info("[REDIS SUBSCRIBER] Nhận alert: type={}, message={}",
                    alert.getType(), alert.getMessage());

        } catch (Exception e) {
            log.error("Lỗi parse message từ Redis: {}", e.getMessage());
        }
    }
}
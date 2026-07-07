package org.example.medicinecachedemo.config;

import org.example.medicinecachedemo.service.RedisMessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@Configuration
@Slf4j
public class RedisConfig {

    @Autowired
    private RedisMessageSubscriber redisMessageSubscriber;

    private static final String CHANNEL_NAME = "pharmacy-alerts";


    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                new MessageListenerAdapter(redisMessageSubscriber),
                new ChannelTopic(CHANNEL_NAME)
        );

        log.info("=== ĐÃ ĐĂNG KÝ SUBSCRIBER LẮNG NGHE CHANNEL: {} ===", CHANNEL_NAME);
        return container;
    }
}
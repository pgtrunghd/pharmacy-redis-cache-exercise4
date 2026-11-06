package com.example.pharmacy.config;

import com.example.pharmacy.service.PharmacyAlertPublisher;
import com.example.pharmacy.service.PharmacyAlertSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, MessageListenerAdapter pharmacyAlertListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(pharmacyAlertListener, new ChannelTopic(PharmacyAlertPublisher.CHANNEL));
        return container;
    }

    @Bean
    MessageListenerAdapter pharmacyAlertListener(PharmacyAlertSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}

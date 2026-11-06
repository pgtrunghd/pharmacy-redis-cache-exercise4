package com.example.pharmacy.service;

import com.example.pharmacy.dto.PharmacyAlert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PharmacyAlertPublisher {
    public static final String CHANNEL = "pharmacy-alerts";
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public PharmacyAlertPublisher(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void publish(PharmacyAlert alert) {
        try {
            redis.convertAndSend(CHANNEL, objectMapper.writeValueAsString(alert));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize pharmacy alert", e);
        }
    }
}

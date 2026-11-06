package com.example.pharmacy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PharmacyAlertSubscriber {
    private static final Logger log = LoggerFactory.getLogger(PharmacyAlertSubscriber.class);

    public void onMessage(String message) {
        log.info("Received pharmacy-alerts message: {}", message);
    }
}

package com.bank.bankapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${app.kafka.transaction-topic}")
    private String transactionTopic;

    public void sendTransactionNotification(String message) {
        // This is non-blocking. The API continues immediately.
        kafkaTemplate.send(transactionTopic, message);
    }
}

package com.bank.bankapp.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "${app.kafka.transaction-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("Kafka notification received: {}", message);
        // Here is where you would call an Email API or SMS Gateway
    }
}

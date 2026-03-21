package com.bank.bankapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class BankAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAppApplication.class, args);
    }

}

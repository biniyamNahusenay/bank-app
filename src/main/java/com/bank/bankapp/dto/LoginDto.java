package com.bank.bankapp.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String usernameOrEmail;
    private String password;

    // getters and setters
}

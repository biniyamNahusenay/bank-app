package com.bank.bankapp.dto;

import lombok.Data;

@Data
public class JwtAuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;

}

package com.bank.bankapp.service;
import com.bank.bankapp.dto.JwtAuthResponse;
import com.bank.bankapp.dto.RegisterDto;
import com.bank.bankapp.dto.LoginDto;
import com.bank.bankapp.dto.RefreshTokenRequest;

public interface AuthService {

    String register(RegisterDto registerDto);

    JwtAuthResponse login(LoginDto loginDto);

    JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}

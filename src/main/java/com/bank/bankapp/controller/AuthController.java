package com.bank.bankapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.bankapp.dto.JwtAuthResponse;
import com.bank.bankapp.dto.LoginDto;
import com.bank.bankapp.dto.RefreshTokenRequest;
import com.bank.bankapp.dto.RegisterDto;
import com.bank.bankapp.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse jwtAuthResponse = authService.login(loginDto);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtAuthResponse jwtAuthResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }
}

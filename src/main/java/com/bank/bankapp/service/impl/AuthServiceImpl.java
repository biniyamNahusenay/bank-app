package com.bank.bankapp.service.impl;

import com.bank.bankapp.dto.JwtAuthResponse;
import com.bank.bankapp.dto.LoginDto;
import com.bank.bankapp.dto.RefreshTokenRequest;
import com.bank.bankapp.dto.RegisterDto;
import com.bank.bankapp.entity.User;
import com.bank.bankapp.repository.UserRepository;
import com.bank.bankapp.security.JwtTokenProvider;
import com.bank.bankapp.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return "Username is already taken.";
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return "Email is already taken.";
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "User registered successfully!.";
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds());

        return jwtAuthResponse;
    }

    @Override
    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        String accessToken = jwtTokenProvider.generateAccessTokenFromUsername(username);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds());
        return jwtAuthResponse;
    }
}

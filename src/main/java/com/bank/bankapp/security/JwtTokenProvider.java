package com.bank.bankapp.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Correct
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.jwt-refresh-expiration-milliseconds}")
    private long refreshTokenExpirationDate;

    // Generate JWT token
    public String generateAccessToken(Authentication authentication) {
        return buildToken(authentication.getName(), jwtExpirationDate, "access");
    }

    public String generateRefreshToken(Authentication authentication) {
        return buildToken(authentication.getName(), refreshTokenExpirationDate, "refresh");
    }

    public String generateAccessTokenFromUsername(String username) {
        return buildToken(username, jwtExpirationDate, "access");
    }

    private String buildToken(String username, long expiration, String tokenType) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expiration);
        return Jwts.builder()
                .subject(username)
                .claim("type", tokenType)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Get username from JWT token
    public String getUsername(String token) {
        return getClaims(token)
                .getSubject();
    }

    public boolean validateAccessToken(String token) {
        return validateTokenByType(token, "access");
    }

    public boolean validateRefreshToken(String token) {
        return validateTokenByType(token, "refresh");
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtExpirationDate / 1000;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                ;
    }

    private boolean validateTokenByType(String token, String expectedType) {
        try {
            Claims claims = getClaims(token);
            return expectedType.equals(claims.get("type", String.class));
        } catch (Exception e) {
            // In a real app, log specific exceptions (Expired, Malformed, etc.)
            return false;
        }
    }
}

package com.cooperative.cabin.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpirationMinutes;
    private final long refreshExpirationDays;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.accessExpirationMinutes}") long accessExpirationMinutes,
            @Value("${security.jwt.refreshExpirationDays}") long refreshExpirationDays) {
        byte[] keyBytes = Decoders.BASE64.decode(encodeIfNotBase64(secret));
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = getAllClaims(token);
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public Long extractUserId(String token) {
        Claims claims = getAllClaims(token);
        Object userId = claims.get("userId");
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        return null;
    }

    public String generateAccessToken(String username, String role, Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("roles", List.of(role), "userId", userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpirationMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpirationDays, ChronoUnit.DAYS)))
                .signWith(signingKey)
                .compact();
    }

    public String refreshAccessToken(String refreshToken, Long userId, String role) {
        Claims claims = getAllClaims(refreshToken);
        if (!"refresh".equals(claims.get("type"))) {
            throw new IllegalArgumentException("Token de refresh inválido");
        }
        String username = claims.getSubject();
        return generateAccessToken(username, role, userId);
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    private String encodeIfNotBase64(String secret) {
        // Si el secreto ya viene en Base64, usamos tal cual; si no, lo codificamos a
        // Base64
        try {
            Decoders.BASE64.decode(secret);
            return secret; // ya es base64
        } catch (Exception ignored) {
            return java.util.Base64.getEncoder()
                    .encodeToString(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}

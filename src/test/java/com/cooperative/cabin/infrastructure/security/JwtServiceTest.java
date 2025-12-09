package com.cooperative.cabin.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("this-is-a-very-long-secret-key-for-tests-1234567890", 5, 1);

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken("user@test.com", "ADMIN", 10L);

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@test.com");
        assertThat(jwtService.extractRoles(token)).contains("ADMIN");
        assertThat(jwtService.extractUserId(token)).isEqualTo(10L);
    }

    @Test
    void shouldRefreshAccessTokenFromRefreshToken() {
        String refresh = jwtService.generateRefreshToken("user@test.com");

        String newAccess = jwtService.refreshAccessToken(refresh, 10L, "ADMIN");

        assertThat(jwtService.extractUsername(newAccess)).isEqualTo("user@test.com");
        assertThat(jwtService.extractRoles(newAccess)).contains("ADMIN");
    }
}


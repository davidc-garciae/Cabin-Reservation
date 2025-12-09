package com.cooperative.cabin.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private JwtAuthFilter filter;

    @BeforeEach
    void setup() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPopulateSecurityContextWhenBearerPresent() throws Exception {
        String token = "token";
        when(jwtService.extractUsername(token)).thenReturn("user@test.com");
        when(jwtService.extractUserId(token)).thenReturn(5L);
        when(jwtService.extractRoles(token)).thenReturn(List.of("ADMIN"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority").contains("ROLE_ADMIN");
        assertThat(request.getAttribute("userId")).isEqualTo(5L);
        verify(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void shouldSkipWhenNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}


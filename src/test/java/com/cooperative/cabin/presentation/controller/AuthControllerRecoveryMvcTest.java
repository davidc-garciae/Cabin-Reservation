package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestMvcConfiguration.class)
@ActiveProfiles("test")
class AuthControllerRecoveryMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthApplicationService authApplicationService;

        @MockBean
        private JwtService jwtService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void recoverPassword_validEmail_returns200() throws Exception {
                // Given
                String email = "test@example.com";
                willDoNothing().given(authApplicationService).recoverPassword(email);

                // When & Then
                mockMvc.perform(post("/api/auth/recover-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("email", email))))
                                .andExpect(status().isOk());
        }

        @Test
        void recoverPassword_invalidEmail_returns400() throws Exception {
                // Given
                String invalidEmail = "invalid-email";

                // When & Then
                mockMvc.perform(post("/api/auth/recover-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("email", invalidEmail))))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void resetPassword_validToken_returns200() throws Exception {
                // Given
                String token = "valid-token";
                String newPin = "1234";
                willDoNothing().given(authApplicationService).resetPassword(token, newPin);

                // When & Then
                mockMvc.perform(post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "token", token,
                                                "newPin", newPin))))
                                .andExpect(status().isOk());
        }

        @Test
        void resetPassword_invalidToken_returns400() throws Exception {
                // Given
                String invalidToken = "invalid-token";
                String newPin = "1234";
                willThrow(new IllegalArgumentException("Token inválido"))
                                .given(authApplicationService).resetPassword(invalidToken, newPin);

                // When & Then
                mockMvc.perform(post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "token", invalidToken,
                                                "newPin", newPin))))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void resetPassword_invalidPin_returns400() throws Exception {
                // Given
                String token = "valid-token";
                String invalidPin = "12"; // Muy corto

                // When & Then
                mockMvc.perform(post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "token", token,
                                                "newPin", invalidPin))))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void validateToken_validToken_returns200() throws Exception {
                // Given
                String token = "valid-jwt-token";
                given(authApplicationService.validateToken(token)).willReturn(true);

                // When & Then
                mockMvc.perform(post("/api/auth/validate-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("token", token))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        void validateToken_invalidToken_returns200WithFalse() throws Exception {
                // Given
                String token = "invalid-jwt-token";
                given(authApplicationService.validateToken(token)).willReturn(false);

                // When & Then
                mockMvc.perform(post("/api/auth/validate-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("token", token))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        void validateToken_missingToken_returns400() throws Exception {
                // When & Then
                mockMvc.perform(post("/api/auth/validate-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest());
        }
}

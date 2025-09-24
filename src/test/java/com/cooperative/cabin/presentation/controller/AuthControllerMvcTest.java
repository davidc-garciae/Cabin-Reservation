package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.infrastructure.repository.PasswordResetTokenRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AuthControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthApplicationService authApplicationService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private UserJpaRepository userRepository;

        @MockBean
        private PasswordResetTokenRepository tokenRepository;

        @Test
        void login_returnsAccessAndRefreshTokens() throws Exception {
                given(authApplicationService.login(anyString(), anyString()))
                                .willReturn(Map.of("accessToken", "access.jwt", "refreshToken", "refresh.jwt"));

                String body = "{" +
                                "\"username\":\"john@example.com\"," +
                                "\"password\":\"secret\"" +
                                "}";

                mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("access.jwt"))
                                .andExpect(jsonPath("$.refreshToken").value("refresh.jwt"));
        }

        @Test
        void refresh_returnsNewAccessToken() throws Exception {
                given(authApplicationService.refreshToken(anyString()))
                                .willReturn(Map.of("accessToken", "new.access.jwt"));

                String body = "{" +
                                "\"refreshToken\":\"refresh.jwt\"" +
                                "}";

                mockMvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("new.access.jwt"));
        }
}

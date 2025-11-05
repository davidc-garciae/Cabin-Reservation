package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AdminUserApplicationService;
import com.cooperative.cabin.application.service.AuthApplicationService;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class UserProfileControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AdminUserApplicationService adminUserApplicationService;

        @MockBean
        private AuthApplicationService authApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void getProfile_returnsUser() throws Exception {
                AdminUserResponse response = new AdminUserResponse(7L, "john.doe@example.com", "12345678", "John Doe",
                                "PROFESSOR",
                                true);
                given(adminUserApplicationService.getProfile(7L)).willReturn(response);

                mockMvc.perform(get("/api/users/profile").header("X-User-Id", "7"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(7))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        void putProfile_updatesUser() throws Exception {
                AdminUserResponse response = new AdminUserResponse(7L, "johnny@example.com", "87654321", "Johnny",
                                "PROFESSOR",
                                true);
                given(adminUserApplicationService.updateProfile(7L, "johnny@example.com", "87654321", "Johnny"))
                                .willReturn(response);

                String body = "{" +
                                "\"email\":\"johnny@example.com\"," +
                                "\"documentNumber\":\"87654321\"," +
                                "\"fullName\":\"Johnny\"" +
                                "}";

                mockMvc.perform(put("/api/users/profile").with(csrf())
                                .header("X-User-Id", "7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("johnny@example.com"))
                                .andExpect(jsonPath("$.fullName").value("Johnny"));
        }

        @Test
        void changePassword_updatesSuccessfully() throws Exception {
                given(authApplicationService.changePassword(7L, "1234", "SecurePass123"))
                                .willReturn(Map.of("message", "Contraseña actualizada exitosamente", "mustChangePassword", false));

                String body = "{" +
                                "\"currentPassword\":\"1234\"," +
                                "\"newPassword\":\"SecurePass123\"" +
                                "}";

                mockMvc.perform(put("/api/users/change-password").with(csrf())
                                .header("X-User-Id", "7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Contraseña actualizada exitosamente"))
                                .andExpect(jsonPath("$.mustChangePassword").value(false));
        }
}

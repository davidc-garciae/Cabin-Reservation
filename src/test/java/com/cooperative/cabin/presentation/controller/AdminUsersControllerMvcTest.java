package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUsersController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminUsersControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private com.cooperative.cabin.application.service.AdminUserApplicationService adminUserApplicationService;

        @Test
        void getUsers_returnsList() throws Exception {
                AdminUserResponse u1 = new AdminUserResponse(1L, "john@example.com", "12345678", "John Doe", "USER",
                                true);
                AdminUserResponse u2 = new AdminUserResponse(2L, "admin@example.com", "ADMIN001", "Admin", "ADMIN",
                                true);
                when(adminUserApplicationService.listUsers(any(), any(), any(), any())).thenReturn(List.of(u1, u2));

                mockMvc.perform(get("/api/admin/users").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[1].role").value("ADMIN"));
        }

        @Test
        void getUsers_filteredByEmail_returnsSingle() throws Exception {
                AdminUserResponse u1 = new AdminUserResponse(5L, "filter@example.com", "87654321", "Filtered", "USER", true);
                when(adminUserApplicationService.listUsers(eq("filter@example.com"), eq(null), any(), any()))
                                .thenReturn(List.of(u1));

                mockMvc.perform(get("/api/admin/users").param("email", "filter@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].email").value("filter@example.com"));
        }

        @Test
        void getUsers_filteredByDocument_returnsSingle() throws Exception {
                AdminUserResponse u1 = new AdminUserResponse(6L, "doc@example.com", "99887766", "Doc", "USER", true);
                when(adminUserApplicationService.listUsers(eq(null), eq("99887766"), any(), any()))
                                .thenReturn(List.of(u1));

                mockMvc.perform(get("/api/admin/users").param("documentNumber", "99887766"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].documentNumber").value("99887766"));
        }

        @Test
        void getUserById_returnsUser() throws Exception {
                AdminUserResponse u1 = new AdminUserResponse(5L, "u5@example.com", "87654321", "U Five", "USER", true);
                when(adminUserApplicationService.getById(5L)).thenReturn(u1);

                mockMvc.perform(get("/api/admin/users/5").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("u5@example.com"));
        }

        @Test
        void forcePasswordChange_returnsOkMessage() throws Exception {
                doNothing().when(adminUserApplicationService).forcePasswordChange(7L);

                mockMvc.perform(post("/api/admin/users/7/force-password-change").with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Usuario debe cambiar su contraseña en el próximo login"));
        }

        @Test
        void deleteUser_ok() throws Exception {
                doNothing().when(adminUserApplicationService).deleteUser(7L);

                mockMvc.perform(delete("/api/admin/users/7").with(csrf()))
                                .andExpect(status().isOk());
        }
}

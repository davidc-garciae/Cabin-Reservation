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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                AdminUserResponse u1 = new AdminUserResponse(1L, "john@example.com", "John Doe", "USER", true);
                AdminUserResponse u2 = new AdminUserResponse(2L, "admin@example.com", "Admin", "ADMIN", true);
                when(adminUserApplicationService.listUsers()).thenReturn(List.of(u1, u2));

                mockMvc.perform(get("/api/admin/users").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[1].role").value("ADMIN"));
        }

        @Test
        void getUserById_returnsUser() throws Exception {
                AdminUserResponse u1 = new AdminUserResponse(5L, "u5@example.com", "U Five", "USER", true);
                when(adminUserApplicationService.getById(5L)).thenReturn(u1);

                mockMvc.perform(get("/api/admin/users/5").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("u5@example.com"));
        }

        @Test
        void deleteUser_noContent() throws Exception {
                doNothing().when(adminUserApplicationService).deleteUser(anyLong());

                mockMvc.perform(delete("/api/admin/users/7").with(csrf()))
                                .andExpect(status().isNoContent());
        }
}

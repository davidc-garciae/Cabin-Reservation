package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.ConfigurationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminConfigurationsController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminConfigurationsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigurationService configurationService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listAllConfigs() throws Exception {
        when(configurationService.getAll()).thenReturn(Map.of("reservation.timeout.minutes", "60"));
        mockMvc.perform(get("/api/admin/configurations").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateConfig() throws Exception {
        mockMvc.perform(put("/api/admin/configurations/reservation.timeout.minutes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\":\"90\"}"))
                .andExpect(status().isOk());
    }
}

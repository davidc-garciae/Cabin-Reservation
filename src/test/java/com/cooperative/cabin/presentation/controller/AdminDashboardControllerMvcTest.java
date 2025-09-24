package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AdminDashboardService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.AdminDashboardResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ TestMvcConfiguration.class, com.cooperative.cabin.MethodSecurityTestConfig.class })
@ActiveProfiles("test")
class AdminDashboardControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService dashboardService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getSummary_withAdminRole_returns200AndPayload() throws Exception {
        AdminDashboardResponse resp = new AdminDashboardResponse(124, 8, 32, 5, 70, 9, 340, 5, 65, 0, 0, 0, 0);
        given(dashboardService.getSummary()).willReturn(resp);

        mockMvc.perform(get("/api/admin/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationsTotal").value(124))
                .andExpect(jsonPath("$.reservationsPending").value(8))
                .andExpect(jsonPath("$.activeUsers").value(340))
                .andExpect(jsonPath("$.activeCabins").value(5))
                .andExpect(jsonPath("$.occupancyRatePercent").value(65));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getSummary_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

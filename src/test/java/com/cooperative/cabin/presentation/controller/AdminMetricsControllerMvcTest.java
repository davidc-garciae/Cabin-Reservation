package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.AdminMetricsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMetricsController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminMetricsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AdminMetricsService adminMetricsService;

    @Test
    void getMetrics_returnsSummary() throws Exception {
        AdminMetricsResponse dto = new AdminMetricsResponse();
        dto.setReservationsCreated(5);
        dto.setReservationsCancelled(2);
        dto.setStatusTransitions(7);
        dto.setSchedulerTransitions(3);

        when(adminMetricsService.getSummary()).thenReturn(dto);

        mockMvc.perform(get("/api/admin/metrics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationsCreated").value(5))
                .andExpect(jsonPath("$.reservationsCancelled").value(2))
                .andExpect(jsonPath("$.statusTransitions").value(7))
                .andExpect(jsonPath("$.schedulerTransitions").value(3));
    }
}

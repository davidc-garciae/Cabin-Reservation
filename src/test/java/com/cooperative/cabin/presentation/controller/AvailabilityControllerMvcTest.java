package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AvailabilityApplicationService;
import com.cooperative.cabin.presentation.dto.AvailabilityDayResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
@ActiveProfiles("test")
@Import(com.cooperative.cabin.TestMvcConfiguration.class)
@org.springframework.security.test.context.support.WithMockUser
class AvailabilityControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityApplicationService service;

    @MockitoBean
    private com.cooperative.cabin.infrastructure.security.JwtService jwtService;

    @Test
    void shouldReturnAvailableDates() throws Exception {
        when(service.getAvailableDates()).thenReturn(List.of("2025-01-01", "2025-01-02"));

        mockMvc.perform(get("/api/availability").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("2025-01-01"));
    }

    @Test
    void shouldReturnCalendarMap() throws Exception {
        when(service.getAvailabilityCalendar(2025, 1)).thenReturn(Map.of("2025-01-01", true));

        mockMvc.perform(get("/api/availability/calendar")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['2025-01-01']").value(true));
    }

    @Test
    void shouldReturnCalendarList() throws Exception {
        when(service.getAvailabilityCalendar(2025, 1)).thenReturn(Map.of("2025-01-01", true));

        mockMvc.perform(get("/api/availability/calendar/list")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-01-01"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
}
 
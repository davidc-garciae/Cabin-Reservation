package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AvailabilityApplicationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AvailabilityControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AvailabilityApplicationService availabilityApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void getAvailability_returnsDates() throws Exception {
                given(availabilityApplicationService.getAvailableDates())
                                .willReturn(List.of("2025-01-10", "2025-01-11"));

                mockMvc.perform(get("/api/availability"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0]").value("2025-01-10"));
        }

        @Test
        void getCalendar_returnsCalendarMap() throws Exception {
                given(availabilityApplicationService.getAvailabilityCalendar(2025, 1))
                                .willReturn(Map.of(
                                                "2025-01-10", true,
                                                "2025-01-11", false));

                mockMvc.perform(get("/api/availability/calendar")
                                .param("year", "2025")
                                .param("month", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.['2025-01-10']").value(true))
                                .andExpect(jsonPath("$.['2025-01-11']").value(false));
        }
}

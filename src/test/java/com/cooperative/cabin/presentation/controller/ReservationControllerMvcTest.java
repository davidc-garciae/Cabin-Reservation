package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class ReservationControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ReservationApplicationService reservationApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void createPreReservation_returnsCreatedReservation() throws Exception {
                User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(2L, "Test Cabin", 4);
                Reservation created = TestEntityFactory.createReservation(user, cabin,
                                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2, ReservationStatus.PENDING);
                created.setId(10L);

                given(reservationApplicationService.createPreReservation(
                                eq(1L), eq(2L), eq(LocalDate.of(2025, 1, 10)), eq(LocalDate.of(2025, 1, 12)), eq(2)))
                                .willReturn(created);

                String body = "{" +
                                "\"userId\":1," +
                                "\"cabinId\":2," +
                                "\"startDate\":\"2025-01-10\"," +
                                "\"endDate\":\"2025-01-12\"," +
                                "\"guests\":2" +
                                "}";

                mockMvc.perform(post("/api/reservations").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        void cancelReservation_returnsCancelledReservation() throws Exception {
                User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(2L, "Test Cabin", 4);
                Reservation cancelled = TestEntityFactory.createReservation(user, cabin,
                                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2, ReservationStatus.CANCELLED);
                cancelled.setId(10L);

                given(reservationApplicationService.cancelByUser(eq(1L), eq(10L))).willReturn(cancelled);

                mockMvc.perform(delete("/api/reservations/{id}", 10).with(csrf())
                                .header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.status").value("CANCELLED"));
        }
}

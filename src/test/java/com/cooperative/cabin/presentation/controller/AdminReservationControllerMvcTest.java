package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.model.Cabin;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = AdminReservationController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminReservationControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ReservationApplicationService reservationApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void changeStatus_returnsUpdatedReservation() throws Exception {
                User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(2L, "Test Cabin", 4);
                Reservation updated = TestEntityFactory.createReservation(user, cabin,
                                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2, ReservationStatus.CONFIRMED);
                updated.setId(10L);

                given(reservationApplicationService.changeStatusByAdmin(eq(10L), eq(ReservationStatus.CONFIRMED)))
                                .willReturn(updated);

                String body = "{\"status\":\"CONFIRMED\"}";

                mockMvc.perform(patch("/api/admin/reservations/{id}/status", 10).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        }
}

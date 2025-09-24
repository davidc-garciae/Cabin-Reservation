package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationQueryController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class ReservationQueryControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ReservationApplicationService reservationApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void listReservations_returnsArray() throws Exception {
                List<Reservation> list = List.of(
                                new Reservation(1L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(1), 2,
                                                ReservationStatus.PENDING));
                given(reservationApplicationService.listByUser(eq(1L))).willReturn(list);

                mockMvc.perform(get("/api/reservations").header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void getReservation_returnsItem() throws Exception {
                Reservation r = new Reservation(5L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(1), 2,
                                ReservationStatus.PENDING);
                given(reservationApplicationService.getByIdForUser(eq(1L), eq(5L))).willReturn(r);

                mockMvc.perform(get("/api/reservations/{id}", 5).header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(5));
        }
}

package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.infrastructure.security.JwtService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = AdminReservationsController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminReservationsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationApplicationService service;

    @MockBean
    private JwtService jwtService;

    @Test
    void listAll_returnsList() throws Exception {
        List<Reservation> list = List.of(
                new Reservation(3L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(1), 2,
                        ReservationStatus.PENDING));
        given(service.listAllForAdmin()).willReturn(list);
        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(3));
    }

    @Test
    void delete_removesReservation() throws Exception {
        willDoNothing().given(service).deleteByAdmin(eq(3L));
        mockMvc.perform(delete("/api/admin/reservations/{id}", 3).with(csrf()))
                .andExpect(status().isNoContent());
    }
}

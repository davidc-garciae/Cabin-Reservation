package com.cooperative.cabin.presentation.dto;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.presentation.mapper.ReservationMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDtosTest {

    @Test
    void mapsDomainToResponse() {
        Reservation r = new Reservation(10L, 1L, 2L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2,
                ReservationStatus.PENDING);
        ReservationResponse dto = ReservationMapper.INSTANCE.toResponse(r);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getCabinId()).isEqualTo(2L);
        assertThat(dto.getStatus()).isEqualTo("PENDING");
        assertThat(dto.getStartDate()).isEqualTo("2025-01-10");
        assertThat(dto.getEndDate()).isEqualTo("2025-01-12");
    }

    @Test
    void mapsRequestToDomain() {
        CreateReservationRequest req = new CreateReservationRequest(1L, 2L, "2025-01-10", "2025-01-12", 2);
        Reservation r = ReservationMapper.INSTANCE.fromCreateRequest(req);

        assertThat(r.getUserId()).isEqualTo(1L);
        assertThat(r.getCabinId()).isEqualTo(2L);
        assertThat(r.getStartDate()).isEqualTo(LocalDate.parse("2025-01-10"));
        assertThat(r.getEndDate()).isEqualTo(LocalDate.parse("2025-01-12"));
        assertThat(r.getNumberOfGuests()).isEqualTo(2);
    }
}



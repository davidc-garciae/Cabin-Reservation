package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.presentation.dto.CreateReservationRequest;
import com.cooperative.cabin.presentation.dto.ReservationResponse;

import java.time.LocalDate;

public class ReservationMapper {
    public static final ReservationMapper INSTANCE = new ReservationMapper();

    public ReservationResponse toResponse(Reservation r) {
        if (r == null)
            return null;
        return new ReservationResponse(
                r.getId(),
                r.getUserId(),
                r.getCabinId(),
                r.getStartDate() != null ? r.getStartDate().toString() : null,
                r.getEndDate() != null ? r.getEndDate().toString() : null,
                r.getNumberOfGuests(),
                r.getStatus() != null ? r.getStatus().name() : null);
    }

    public Reservation fromCreateRequest(CreateReservationRequest req) {
        if (req == null)
            return null;
        LocalDate start = req.getStartDate() != null ? LocalDate.parse(req.getStartDate()) : null;
        LocalDate end = req.getEndDate() != null ? LocalDate.parse(req.getEndDate()) : null;
        return new Reservation(
                null,
                req.getUserId(),
                req.getCabinId(),
                start,
                end,
                req.getGuests(),
                (ReservationStatus) null);
    }
}

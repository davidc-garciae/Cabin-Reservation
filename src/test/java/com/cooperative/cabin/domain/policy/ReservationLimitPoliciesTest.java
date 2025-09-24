package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationLimitPoliciesTest {

    @Test
    void withinAnnualLimit_true_whenBelowMax() {
        int year = 2025;
        Long userId = 1L;
        List<Reservation> list = List.of(
                new Reservation(1L, userId, 1L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2,
                        ReservationStatus.COMPLETED),
                new Reservation(2L, userId, 1L, LocalDate.of(2025, 3, 5), LocalDate.of(2025, 3, 8), 2,
                        ReservationStatus.CANCELLED));
        assertTrue(ReservationLimitPolicies.withinAnnualLimit(userId, list, 4, year));
    }

    @Test
    void withinAnnualLimit_false_whenAtOrAboveMax() {
        int year = 2025;
        Long userId = 1L;
        List<Reservation> list = List.of(
                new Reservation(1L, userId, 1L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), 2,
                        ReservationStatus.COMPLETED),
                new Reservation(2L, userId, 1L, LocalDate.of(2025, 3, 5), LocalDate.of(2025, 3, 8), 2,
                        ReservationStatus.CANCELLED),
                new Reservation(3L, userId, 1L, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 3), 2,
                        ReservationStatus.COMPLETED),
                new Reservation(4L, userId, 1L, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 2), 2,
                        ReservationStatus.CANCELLED));
        assertFalse(ReservationLimitPolicies.withinAnnualLimit(userId, list, 4, year));
    }
}

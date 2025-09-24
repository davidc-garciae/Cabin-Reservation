package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationPoliciesTest {

    @Test
    void hasActiveReservation_true_whenPendingOrConfirmedOrInUse() {
        Long userId = 1L;
        List<Reservation> reservations = List.of(
                new Reservation(10L, userId, 100L, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.PENDING),
                new Reservation(11L, 2L, 100L, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.CANCELLED));
        assertTrue(ReservationPolicies.hasActiveReservation(userId, reservations));
    }

    @Test
    void hasActiveReservation_false_whenOnlyCompletedOrCancelled() {
        Long userId = 1L;
        List<Reservation> reservations = List.of(
                new Reservation(10L, userId, 100L, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.COMPLETED),
                new Reservation(11L, userId, 100L, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.CANCELLED));
        assertFalse(ReservationPolicies.hasActiveReservation(userId, reservations));
    }

    @Test
    void isWithinStandardTimeout_true_whenEnoughDaysPassed() {
        LocalDate last = LocalDate.now().minusDays(31);
        assertTrue(ReservationPolicies.isWithinStandardTimeout(last, 30, LocalDate.now()));
    }

    @Test
    void isWithinStandardTimeout_false_whenNotEnoughDays() {
        LocalDate last = LocalDate.now().minusDays(10);
        assertFalse(ReservationPolicies.isWithinStandardTimeout(last, 30, LocalDate.now()));
    }

    @Test
    void isWithinCancellationTimeout_true_whenEnoughDaysPassed() {
        LocalDate last = LocalDate.now().minusDays(46);
        assertTrue(ReservationPolicies.isWithinCancellationTimeout(last, 45, LocalDate.now()));
    }

    @Test
    void isWithinCancellationTimeout_false_whenNotEnoughDays() {
        LocalDate last = LocalDate.now().minusDays(20);
        assertFalse(ReservationPolicies.isWithinCancellationTimeout(last, 45, LocalDate.now()));
    }
}

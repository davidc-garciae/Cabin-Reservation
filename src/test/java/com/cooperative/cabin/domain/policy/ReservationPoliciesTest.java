package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.model.Cabin;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationPoliciesTest {

    @Test
    void hasActiveReservation_true_whenPendingOrConfirmedOrInUse() {
        Long userId = 1L;
        User user1 = TestEntityFactory.createUser(userId, "user1@test.com", "12345678");
        User user2 = TestEntityFactory.createUser(2L, "user2@test.com", "12345679");
        Cabin cabin = TestEntityFactory.createCabin(100L, "Test Cabin", 4);
        List<Reservation> reservations = List.of(
                TestEntityFactory.createReservation(user1, cabin, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.PENDING),
                TestEntityFactory.createReservation(user2, cabin, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.CANCELLED));
        reservations.get(0).setId(10L);
        reservations.get(1).setId(11L);
        assertTrue(ReservationPolicies.hasActiveReservation(userId, reservations));
    }

    @Test
    void hasActiveReservation_false_whenOnlyCompletedOrCancelled() {
        Long userId = 1L;
        User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
        Cabin cabin = TestEntityFactory.createCabin(100L, "Test Cabin", 4);
        List<Reservation> reservations = List.of(
                TestEntityFactory.createReservation(user, cabin, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.COMPLETED),
                TestEntityFactory.createReservation(user, cabin, LocalDate.now(), LocalDate.now().plusDays(2), 2,
                        ReservationStatus.CANCELLED));
        reservations.get(0).setId(10L);
        reservations.get(1).setId(11L);
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

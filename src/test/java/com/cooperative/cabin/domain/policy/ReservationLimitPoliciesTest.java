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

class ReservationLimitPoliciesTest {

        @Test
        void withinAnnualLimit_true_whenBelowMax() {
                int year = 2025;
                Long userId = 1L;
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                List<Reservation> list = List.of(
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 1, 10),
                                                LocalDate.of(2025, 1, 12), 2,
                                                ReservationStatus.COMPLETED),
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 3, 5),
                                                LocalDate.of(2025, 3, 8), 2,
                                                ReservationStatus.CANCELLED));
                list.get(0).setId(1L);
                list.get(1).setId(2L);
                assertTrue(ReservationLimitPolicies.withinAnnualLimit(userId, list, 4, year));
        }

        @Test
        void withinAnnualLimit_false_whenAtOrAboveMax() {
                int year = 2025;
                Long userId = 1L;
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                List<Reservation> list = List.of(
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 1, 10),
                                                LocalDate.of(2025, 1, 12), 2,
                                                ReservationStatus.COMPLETED),
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 3, 5),
                                                LocalDate.of(2025, 3, 8), 2,
                                                ReservationStatus.CANCELLED),
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 6, 1),
                                                LocalDate.of(2025, 6, 3), 2,
                                                ReservationStatus.COMPLETED),
                                TestEntityFactory.createReservation(user, cabin, LocalDate.of(2025, 8, 1),
                                                LocalDate.of(2025, 8, 2), 2,
                                                ReservationStatus.CANCELLED));
                list.get(0).setId(1L);
                list.get(1).setId(2L);
                list.get(2).setId(3L);
                list.get(3).setId(4L);
                assertFalse(ReservationLimitPolicies.withinAnnualLimit(userId, list, 4, year));
        }
}

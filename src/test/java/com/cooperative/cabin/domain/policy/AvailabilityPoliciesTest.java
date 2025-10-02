package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityPoliciesTest {

        @Test
        void respectsMinimumStayPolicy_shouldReturnTrue_whenStayMeetsMinimum() {
                LocalDate startDate = LocalDate.of(2025, 2, 1);
                LocalDate endDate = LocalDate.of(2025, 2, 3);
                int minDays = 2;

                boolean result = AvailabilityPolicies.respectsMinimumStayPolicy(startDate, endDate, minDays);

                assertTrue(result);
        }

        @Test
        void respectsMinimumStayPolicy_shouldReturnFalse_whenStayBelowMinimum() {
                LocalDate startDate = LocalDate.of(2025, 2, 1);
                LocalDate endDate = LocalDate.of(2025, 2, 2);
                int minDays = 2;

                boolean result = AvailabilityPolicies.respectsMinimumStayPolicy(startDate, endDate, minDays);

                assertFalse(result);
        }

        @Test
        void respectsMinimumStayPolicy_shouldReturnTrue_whenNoMinimum() {
                LocalDate startDate = LocalDate.of(2025, 2, 1);
                LocalDate endDate = LocalDate.of(2025, 2, 2);
                int minDays = 0;

                boolean result = AvailabilityPolicies.respectsMinimumStayPolicy(startDate, endDate, minDays);

                assertTrue(result);
        }

        @Test
        void respectsAdvanceBookingPolicy_shouldReturnTrue_whenWithinAdvanceLimit() {
                LocalDate startDate = LocalDate.now().plusDays(30);
                int maxAdvanceDays = 60;

                boolean result = AvailabilityPolicies.respectsAdvanceBookingPolicy(startDate, maxAdvanceDays);

                assertTrue(result);
        }

        @Test
        void respectsAdvanceBookingPolicy_shouldReturnFalse_whenExceedsAdvanceLimit() {
                LocalDate startDate = LocalDate.now().plusDays(90);
                int maxAdvanceDays = 60;

                boolean result = AvailabilityPolicies.respectsAdvanceBookingPolicy(startDate, maxAdvanceDays);

                assertFalse(result);
        }

        @Test
        void isWithinBookingWindow_shouldReturnTrue_whenWithinWindow() {
                LocalDate startDate = LocalDate.now().plusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(3);

                boolean result = AvailabilityPolicies.isWithinBookingWindow(startDate, endDate);

                assertTrue(result);
        }

        @Test
        void isWithinBookingWindow_shouldReturnFalse_whenTooFarInFuture() {
                LocalDate startDate = LocalDate.now().plusMonths(13);
                LocalDate endDate = LocalDate.now().plusMonths(14);

                boolean result = AvailabilityPolicies.isWithinBookingWindow(startDate, endDate);

                assertFalse(result);
        }

        @Test
        void isWithinBookingWindow_shouldReturnFalse_whenInPast() {
                LocalDate startDate = LocalDate.now().minusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(1);

                boolean result = AvailabilityPolicies.isWithinBookingWindow(startDate, endDate);

                assertFalse(result);
        }

        @Test
        void respectsMinimumDaysBetweenReservations_shouldReturnTrue_whenEnoughDaysBetween() {
                User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);

                Reservation existingReservation = TestEntityFactory.createReservation(
                                user, cabin,
                                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 3),
                                2, com.cooperative.cabin.domain.model.ReservationStatus.CONFIRMED);

                LocalDate newStartDate = LocalDate.of(2025, 2, 6); // 3 días después
                int minDaysBetween = 2;

                boolean result = AvailabilityPolicies.respectsMinimumDaysBetweenReservations(
                                newStartDate, List.of(existingReservation), minDaysBetween);

                assertTrue(result);
        }

        @Test
        void respectsMinimumDaysBetweenReservations_shouldReturnFalse_whenNotEnoughDaysBetween() {
                User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);

                Reservation existingReservation = TestEntityFactory.createReservation(
                                user, cabin,
                                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 3),
                                2, com.cooperative.cabin.domain.model.ReservationStatus.CONFIRMED);

                LocalDate newStartDate = LocalDate.of(2025, 2, 4); // Solo 1 día después
                int minDaysBetween = 2;

                boolean result = AvailabilityPolicies.respectsMinimumDaysBetweenReservations(
                                newStartDate, List.of(existingReservation), minDaysBetween);

                assertFalse(result);
        }

        @Test
        void isDateInPast_shouldReturnTrue_whenDateIsInPast() {
                LocalDate pastDate = LocalDate.now().minusDays(1);

                boolean result = AvailabilityPolicies.isDateInPast(pastDate);

                assertTrue(result);
        }

        @Test
        void isDateInPast_shouldReturnFalse_whenDateIsInFuture() {
                LocalDate futureDate = LocalDate.now().plusDays(1);

                boolean result = AvailabilityPolicies.isDateInPast(futureDate);

                assertFalse(result);
        }

        @Test
        void isValidDateRange_shouldReturnTrue_whenStartBeforeEnd() {
                LocalDate startDate = LocalDate.of(2025, 2, 1);
                LocalDate endDate = LocalDate.of(2025, 2, 3);

                boolean result = AvailabilityPolicies.isValidDateRange(startDate, endDate);

                assertTrue(result);
        }

        @Test
        void isValidDateRange_shouldReturnTrue_whenStartEqualsEnd() {
                LocalDate startDate = LocalDate.of(2025, 2, 1);
                LocalDate endDate = LocalDate.of(2025, 2, 1);

                boolean result = AvailabilityPolicies.isValidDateRange(startDate, endDate);

                assertTrue(result);
        }

        @Test
        void isValidDateRange_shouldReturnFalse_whenStartAfterEnd() {
                LocalDate startDate = LocalDate.of(2025, 2, 3);
                LocalDate endDate = LocalDate.of(2025, 2, 1);

                boolean result = AvailabilityPolicies.isValidDateRange(startDate, endDate);

                assertFalse(result);
        }

        @Test
        void respectsAllAvailabilityPolicies_shouldReturnTrue_whenAllPoliciesRespected() {
                LocalDate startDate = LocalDate.now().plusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(3);
                int minStayDays = 2;
                int maxAdvanceDays = 365;
                int minDaysBetween = 1;
                List<Reservation> existingReservations = List.of();

                boolean result = AvailabilityPolicies.respectsAllAvailabilityPolicies(
                                startDate, endDate, minStayDays, maxAdvanceDays, minDaysBetween, existingReservations);

                assertTrue(result);
        }

        @Test
        void respectsAllAvailabilityPolicies_shouldReturnFalse_whenInvalidDateRange() {
                LocalDate startDate = LocalDate.now().plusDays(3);
                LocalDate endDate = LocalDate.now().plusDays(1); // End before start
                int minStayDays = 2;
                int maxAdvanceDays = 365;
                int minDaysBetween = 1;
                List<Reservation> existingReservations = List.of();

                boolean result = AvailabilityPolicies.respectsAllAvailabilityPolicies(
                                startDate, endDate, minStayDays, maxAdvanceDays, minDaysBetween, existingReservations);

                assertFalse(result);
        }

        @Test
        void respectsAllAvailabilityPolicies_shouldReturnFalse_whenDateInPast() {
                LocalDate startDate = LocalDate.now().minusDays(1);
                LocalDate endDate = LocalDate.now().plusDays(1);
                int minStayDays = 2;
                int maxAdvanceDays = 365;
                int minDaysBetween = 1;
                List<Reservation> existingReservations = List.of();

                boolean result = AvailabilityPolicies.respectsAllAvailabilityPolicies(
                                startDate, endDate, minStayDays, maxAdvanceDays, minDaysBetween, existingReservations);

                assertFalse(result);
        }
}
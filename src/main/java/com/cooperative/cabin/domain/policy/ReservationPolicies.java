package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationPolicies {

    public static boolean hasActiveReservation(Long userId, List<Reservation> reservations) {
        return reservations.stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());
    }

    public static boolean isWithinStandardTimeout(LocalDate lastCreatedAtDate, int standardTimeoutDays,
            LocalDate nowDate) {
        if (lastCreatedAtDate == null)
            return true;
        long days = ChronoUnit.DAYS.between(lastCreatedAtDate, nowDate);
        return days >= standardTimeoutDays;
    }

    public static boolean isWithinCancellationTimeout(LocalDate lastCancelledCreatedAtDate, int cancellationTimeoutDays,
            LocalDate nowDate) {
        if (lastCancelledCreatedAtDate == null)
            return true;
        long days = ChronoUnit.DAYS.between(lastCancelledCreatedAtDate, nowDate);
        return days >= cancellationTimeoutDays;
    }
}

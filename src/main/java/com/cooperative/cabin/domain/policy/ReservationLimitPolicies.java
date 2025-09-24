package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public class ReservationLimitPolicies {

    public static boolean withinAnnualLimit(Long userId, List<Reservation> reservationsOfYear, int maxPerYear,
            int year) {
        long count = reservationsOfYear.stream()
                .filter(r -> r.getUserId().equals(userId))
                .filter(r -> r.getStartDate().getYear() == year)
                .count();
        return count < maxPerYear;
    }
}

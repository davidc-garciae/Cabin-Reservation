package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.PriceRange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PricingPolicies {

    public static Optional<PriceRange> findApplicableRange(LocalDate date, Long cabinId, List<PriceRange> ranges) {
        return ranges.stream()
                .filter(r -> r.getCabin().getId().equals(cabinId))
                .filter(r -> !date.isBefore(r.getStartDate()) && !date.isAfter(r.getEndDate()))
                .min(Comparator.comparing(PriceRange::getStartDate).reversed());
    }

    public static boolean hasOverlappingRanges(Long cabinId, List<PriceRange> ranges) {
        List<PriceRange> sorted = ranges.stream()
                .filter(r -> r.getCabin().getId().equals(cabinId))
                .sorted(Comparator.comparing(PriceRange::getStartDate))
                .toList();
        for (int i = 1; i < sorted.size(); i++) {
            PriceRange prev = sorted.get(i - 1);
            PriceRange curr = sorted.get(i);
            boolean overlap = !curr.getStartDate().isAfter(prev.getEndDate());
            if (overlap)
                return true;
        }
        return false;
    }

    public static BigDecimal calculatePrice(BigDecimal defaultBasePrice, PriceRange range) {
        if (range == null)
            return defaultBasePrice;
        BigDecimal base = range.getBasePrice() != null ? range.getBasePrice() : defaultBasePrice;
        BigDecimal multiplier = range.getPriceMultiplier() != null ? range.getPriceMultiplier() : BigDecimal.ONE;
        return base.multiply(multiplier);
    }
}

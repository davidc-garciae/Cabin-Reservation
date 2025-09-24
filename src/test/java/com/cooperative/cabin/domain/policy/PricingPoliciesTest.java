package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.PriceRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingPoliciesTest {

    @Test
    void calculatePrice_usesDefaultWhenNoRange() {
        BigDecimal price = PricingPolicies.calculatePrice(new BigDecimal("100.00"), null);
        assertEquals(new BigDecimal("100.00"), price);
    }

    @Test
    void calculatePrice_appliesRangeBaseAndMultiplier() {
        PriceRange r = new PriceRange(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1), new BigDecimal("200.00"),
                new BigDecimal("1.10"));
        BigDecimal price = PricingPolicies.calculatePrice(new BigDecimal("100.00"), r);
        assertEquals(new BigDecimal("220.0000").stripTrailingZeros(), price.stripTrailingZeros());
    }

    @Test
    void findApplicableRange_picksContainingRange() {
        LocalDate d = LocalDate.of(2025, 1, 15);
        List<PriceRange> ranges = List.of(
                new PriceRange(1L, 1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10), new BigDecimal("100.00"),
                        BigDecimal.ONE),
                new PriceRange(2L, 1L, LocalDate.of(2025, 1, 11), LocalDate.of(2025, 1, 20), new BigDecimal("150.00"),
                        new BigDecimal("1.20")));
        var found = PricingPolicies.findApplicableRange(d, 1L, ranges);
        assertTrue(found.isPresent());
        assertEquals(2L, found.get().getId());
    }

    @Test
    void hasOverlappingRanges_true_whenOverlap() {
        List<PriceRange> ranges = List.of(
                new PriceRange(1L, 1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10), new BigDecimal("100.00"),
                        BigDecimal.ONE),
                new PriceRange(2L, 1L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 15), new BigDecimal("110.00"),
                        BigDecimal.ONE));
        assertTrue(PricingPolicies.hasOverlappingRanges(1L, ranges));
    }

    @Test
    void hasOverlappingRanges_false_whenNoOverlap() {
        List<PriceRange> ranges = List.of(
                new PriceRange(1L, 1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10), new BigDecimal("100.00"),
                        BigDecimal.ONE),
                new PriceRange(2L, 1L, LocalDate.of(2025, 1, 11), LocalDate.of(2025, 1, 15), new BigDecimal("110.00"),
                        BigDecimal.ONE));
        assertFalse(PricingPolicies.hasOverlappingRanges(1L, ranges));
    }
}

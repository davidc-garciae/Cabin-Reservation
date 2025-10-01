package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.User;
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
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                PriceRange r = TestEntityFactory.createPriceRange(cabin, LocalDate.now(), LocalDate.now().plusDays(1),
                                new BigDecimal("200.00"), new BigDecimal("1.10"), "Test reason", admin);
                r.setId(1L);
                BigDecimal price = PricingPolicies.calculatePrice(new BigDecimal("100.00"), r);
                assertEquals(new BigDecimal("220.0000").stripTrailingZeros(), price.stripTrailingZeros());
        }

        @Test
        void findApplicableRange_picksContainingRange() {
                LocalDate d = LocalDate.of(2025, 1, 15);
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                PriceRange r1 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 1, 10),
                                new BigDecimal("100.00"), BigDecimal.ONE, "Test reason", admin);
                r1.setId(1L);
                PriceRange r2 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 11),
                                LocalDate.of(2025, 1, 20),
                                new BigDecimal("150.00"), new BigDecimal("1.20"), "Test reason", admin);
                r2.setId(2L);
                List<PriceRange> ranges = List.of(r1, r2);
                var found = PricingPolicies.findApplicableRange(d, 1L, ranges);
                assertTrue(found.isPresent());
                assertEquals(2L, found.get().getId());
        }

        @Test
        void hasOverlappingRanges_true_whenOverlap() {
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                PriceRange r1 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 1, 10),
                                new BigDecimal("100.00"), BigDecimal.ONE, "Test reason", admin);
                r1.setId(1L);
                PriceRange r2 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 10),
                                LocalDate.of(2025, 1, 15),
                                new BigDecimal("110.00"), BigDecimal.ONE, "Test reason", admin);
                r2.setId(2L);
                List<PriceRange> ranges = List.of(r1, r2);
                assertTrue(PricingPolicies.hasOverlappingRanges(1L, ranges));
        }

        @Test
        void hasOverlappingRanges_false_whenNoOverlap() {
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                PriceRange r1 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 1, 10),
                                new BigDecimal("100.00"), BigDecimal.ONE, "Test reason", admin);
                r1.setId(1L);
                PriceRange r2 = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 1, 11),
                                LocalDate.of(2025, 1, 15),
                                new BigDecimal("110.00"), BigDecimal.ONE, "Test reason", admin);
                r2.setId(2L);
                List<PriceRange> ranges = List.of(r1, r2);
                assertFalse(PricingPolicies.hasOverlappingRanges(1L, ranges));
        }
}

package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.PriceRange;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PriceRangeJpaRepositoryIT {

    @Resource
    private PriceRangeJpaRepository repository;

    @Test
    void saveAndFindByCabinIdAndDateRange() {
        repository.save(new PriceRange(null, 1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), BigDecimal.ONE));
        List<PriceRange> byCabin = repository.findByCabinId(1L);
        assertFalse(byCabin.isEmpty());
        List<PriceRange> byRange = repository.findByCabinIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(1L,
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 15));
        assertEquals(1, byRange.size());
    }
}

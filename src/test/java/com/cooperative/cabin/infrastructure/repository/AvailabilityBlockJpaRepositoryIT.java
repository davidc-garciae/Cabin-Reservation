package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AvailabilityBlockJpaRepositoryIT {

    @Resource
    private AvailabilityBlockJpaRepository repository;

    @Test
    void saveAndFindByCabinId() {
        repository.save(new AvailabilityBlock(null, 1L, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 4)));
        List<AvailabilityBlock> blocks = repository.findByCabinId(1L);
        assertEquals(1, blocks.size());
        assertEquals(LocalDate.of(2025, 4, 1), blocks.get(0).getStartDate());
    }
}

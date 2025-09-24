package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.SystemConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SystemConfigurationJpaRepositoryIT {

    @Resource
    private SystemConfigurationJpaRepository repository;

    @Test
    void saveAndFindByKey() {
        repository.save(new SystemConfiguration(null, "reservation.timeout.minutes", "60"));

        Optional<SystemConfiguration> found = repository.findByConfigKey("reservation.timeout.minutes");
        assertTrue(found.isPresent());
        assertEquals("60", found.get().getConfigValue());
    }
}

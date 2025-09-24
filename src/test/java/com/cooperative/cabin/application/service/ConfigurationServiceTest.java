package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.SystemConfiguration;
import com.cooperative.cabin.infrastructure.repository.SystemConfigurationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.AuditLogJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigurationServiceTest {

    private SystemConfigurationJpaRepository repository;
    private AuditLogJpaRepository auditRepository;
    private ConfigurationServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(SystemConfigurationJpaRepository.class);
        auditRepository = Mockito.mock(AuditLogJpaRepository.class);
        Map<String, String> defaults = Map.of(
                "reservation.timeout.minutes", "60",
                "reservation.penalty.days", "30",
                "jwt.access.minutes", "15",
                "jwt.refresh.days", "7",
                "reservation.max.per.year", "3");
        service = new ConfigurationServiceImpl(repository, defaults, auditRepository);
        when(repository.save(any())).thenAnswer(inv -> {
            SystemConfiguration arg = inv.getArgument(0);
            return new SystemConfiguration(100L, arg.getConfigKey(), arg.getConfigValue());
        });
    }

    @Test
    void returnsDefaultWhenNotStored() {
        when(repository.findByConfigKey("reservation.timeout.minutes")).thenReturn(Optional.empty());
        int minutes = service.getInt("reservation.timeout.minutes");
        assertEquals(60, minutes);
    }

    @Test
    void returnsStoredValue() {
        when(repository.findByConfigKey("reservation.timeout.minutes"))
                .thenReturn(Optional.of(new SystemConfiguration(1L, "reservation.timeout.minutes", "90")));
        int minutes = service.getInt("reservation.timeout.minutes");
        assertEquals(90, minutes);
    }

    @Test
    void parsesDurationMinutes() {
        when(repository.findByConfigKey("reservation.timeout.minutes"))
                .thenReturn(Optional.of(new SystemConfiguration(1L, "reservation.timeout.minutes", "45")));
        Duration d = service.getDurationMinutes("reservation.timeout.minutes");
        assertEquals(Duration.ofMinutes(45), d);
    }

    @Test
    void setValueCreatesOrUpdates() {
        when(repository.findByConfigKey("reservation.max.per.year")).thenReturn(Optional.empty());
        service.setValue("reservation.max.per.year", "5");
        verify(repository).save(argThat(
                cfg -> cfg.getConfigKey().equals("reservation.max.per.year") && cfg.getConfigValue().equals("5")));
        verify(auditRepository).save(Mockito.any());
    }
}

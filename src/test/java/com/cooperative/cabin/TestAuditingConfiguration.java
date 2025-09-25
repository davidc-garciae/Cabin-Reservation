package com.cooperative.cabin;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.Optional;

@TestConfiguration
@Profile("test")
public class TestAuditingConfiguration {

    @Bean
    @Primary
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }

    @Bean
    @Primary
    public AuditorAware<Object> auditorAware() {
        return () -> Optional.of("test-user");
    }
}

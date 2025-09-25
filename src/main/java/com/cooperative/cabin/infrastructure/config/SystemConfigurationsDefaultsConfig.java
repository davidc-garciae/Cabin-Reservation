package com.cooperative.cabin.infrastructure.config;

import com.cooperative.cabin.domain.model.SystemConfiguration;
import com.cooperative.cabin.infrastructure.repository.SystemConfigurationJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SystemConfigurationsDefaultsConfig {

    @Bean
    public Map<String, String> systemConfigurationDefaults() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("reservation.timeout.minutes", "60");
        defaults.put("reservation.penalty.days", "30");
        defaults.put("jwt.access.minutes", "15");
        defaults.put("jwt.refresh.days", "7");
        defaults.put("reservation.max.per.year", "3");
        return defaults;
    }

    @Bean
    public CommandLineRunner systemConfigurationsSeeder(SystemConfigurationJpaRepository repository,
            Map<String, String> systemConfigurationDefaults) {
        return args -> {
            systemConfigurationDefaults.forEach((key, value) -> {
                repository.findByConfigKey(key)
                        .orElseGet(() -> {
                            SystemConfiguration config = new SystemConfiguration(key, value);
                            // Establecer campos de auditoría manualmente
                            config.setCreatedAt(java.time.LocalDateTime.now());
                            config.setUpdatedAt(java.time.LocalDateTime.now());
                            return repository.save(config);
                        });
            });
        };
    }
}

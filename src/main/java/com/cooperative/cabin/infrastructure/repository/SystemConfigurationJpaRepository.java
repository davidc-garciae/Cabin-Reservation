package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemConfigurationJpaRepository extends JpaRepository<SystemConfiguration, Long> {
    Optional<SystemConfiguration> findByConfigKey(String configKey);
}


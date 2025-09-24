package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.SystemConfiguration;
import com.cooperative.cabin.domain.model.AuditLog;
import com.cooperative.cabin.infrastructure.repository.SystemConfigurationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.AuditLogJpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private final SystemConfigurationJpaRepository repository;
    private final Map<String, String> defaults;
    private final AuditLogJpaRepository auditRepository;

    public ConfigurationServiceImpl(SystemConfigurationJpaRepository repository, Map<String, String> defaults,
            AuditLogJpaRepository auditRepository) {
        this.repository = repository;
        this.defaults = new HashMap<>(defaults);
        this.auditRepository = auditRepository;
    }

    @Override
    public String getString(String key) {
        Optional<SystemConfiguration> stored = repository.findByConfigKey(key);
        return stored.map(SystemConfiguration::getConfigValue).orElseGet(() -> defaults.get(key));
    }

    @Override
    public int getInt(String key) {
        String value = getString(key);
        return Integer.parseInt(value);
    }

    @Override
    public Duration getDurationMinutes(String key) {
        return Duration.ofMinutes(getInt(key));
    }

    @Override
    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap<>(defaults);
        repository.findAll().forEach(cfg -> result.put(cfg.getConfigKey(), cfg.getConfigValue()));
        return result;
    }

    @Override
    public void setValue(String key, String value) {
        Optional<SystemConfiguration> existing = repository.findByConfigKey(key);
        if (existing.isPresent()) {
            SystemConfiguration cfg = existing.get();
            String old = cfg.getConfigValue();
            cfg.setConfigValue(value);
            repository.save(cfg);
            auditRepository.save(new AuditLog(null, "UPDATE", "SystemConfiguration", cfg.getId(),
                    "{\"value\":\"" + old + "\"}", "{\"value\":\"" + value + "\"}"));
        } else {
            SystemConfiguration toSave = new SystemConfiguration(null, key, value);
            SystemConfiguration saved = repository.save(toSave);
            Long entityId = saved != null ? saved.getId() : null;
            auditRepository.save(new AuditLog(null, "CREATE", "SystemConfiguration", entityId, null,
                    "{\"value\":\"" + value + "\"}"));
        }
    }
}

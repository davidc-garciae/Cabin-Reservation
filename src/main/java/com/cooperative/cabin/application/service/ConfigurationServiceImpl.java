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
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("La clave de configuración no puede estar vacía");
        }
        if (value == null) {
            throw new IllegalArgumentException("El valor de configuración no puede ser null");
        }
        
        try {
            Optional<SystemConfiguration> existing = repository.findByConfigKey(key);
            if (existing.isPresent()) {
                SystemConfiguration cfg = existing.get();
                String old = cfg.getConfigValue();
                cfg.setConfigValue(value);
                repository.save(cfg);
                // Intentar guardar en audit log (no crítico si falla)
                try {
                    String oldJson = escapeJsonValue(old);
                    String newJson = escapeJsonValue(value);
                    AuditLog auditLog = new AuditLog("UPDATE", "SystemConfiguration", cfg.getId(),
                            "{\"value\":\"" + oldJson + "\"}", "{\"value\":\"" + newJson + "\"}", null, null);
                    auditLog.setCreatedAt(java.time.LocalDateTime.now());
                    auditLog.setUpdatedAt(java.time.LocalDateTime.now());
                    auditRepository.save(auditLog);
                } catch (Exception auditException) {
                    // Log pero no fallar la operación principal
                    System.err.println("Warning: No se pudo guardar en audit log: " + auditException.getMessage());
                }
            } else {
                SystemConfiguration toSave = new SystemConfiguration(key, value);
                SystemConfiguration saved = repository.save(toSave);
                Long entityId = saved != null ? saved.getId() : null;
                // Intentar guardar en audit log (no crítico si falla)
                try {
                    String newJson = escapeJsonValue(value);
                    AuditLog auditLog = new AuditLog("CREATE", "SystemConfiguration", entityId, null,
                            "{\"value\":\"" + newJson + "\"}", null, null);
                    auditLog.setCreatedAt(java.time.LocalDateTime.now());
                    auditLog.setUpdatedAt(java.time.LocalDateTime.now());
                    auditRepository.save(auditLog);
                } catch (Exception auditException) {
                    // Log pero no fallar la operación principal
                    System.err.println("Warning: No se pudo guardar en audit log: " + auditException.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar configuración: " + e.getMessage(), e);
        }
    }
    
    /**
     * Escapa caracteres especiales en valores JSON para evitar inyección o JSON inválido.
     */
    private String escapeJsonValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

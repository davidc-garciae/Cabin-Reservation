package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.cooperative.cabin.TestAuditingConfiguration;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class SystemConfigurationJpaRepositoryIT {

    @Resource
    private SystemConfigurationJpaRepository repository;

    @Resource
    private UserJpaRepository userRepository;

    @Test
    void saveAndFindSystemConfiguration() {
        // Crear usuario administrador
        User admin = createTestAdmin();
        User savedAdmin = userRepository.save(admin);

        // Crear configuración usando método auxiliar
        SystemConfiguration config = createTestSystemConfiguration(
                "reservation.timeout.minutes",
                "30",
                "INT",
                "Tiempo de espera para confirmar reservas",
                savedAdmin);

        // Guardar
        SystemConfiguration savedConfig = repository.save(config);

        // Verificar
        assertNotNull(savedConfig.getId());
        assertEquals("reservation.timeout.minutes", savedConfig.getConfigKey());
        assertEquals("30", savedConfig.getConfigValue());
        assertEquals("INT", savedConfig.getDataType());
        assertEquals("Tiempo de espera para confirmar reservas", savedConfig.getDescription());
        assertEquals(savedAdmin.getId(), savedConfig.getUpdatedBy().getId());
        assertTrue(savedConfig.isActive());
        assertNotNull(savedConfig.getCreatedAt());
        assertNotNull(savedConfig.getUpdatedAt());
    }

    @Test
    void findByConfigKey() {
        // Crear configuración
        SystemConfiguration config = createTestSystemConfiguration(
                "max.reservations.per.year",
                "3",
                "INT",
                "Máximo de reservas por año por usuario",
                null);
        repository.save(config);

        // Buscar por clave
        Optional<SystemConfiguration> found = repository.findByConfigKey("max.reservations.per.year");

        assertTrue(found.isPresent());
        assertEquals("3", found.get().getConfigValue());
        assertEquals("INT", found.get().getDataType());
    }

    @Test
    void findByActiveTrue() {
        // Crear configuraciones activas e inactivas
        SystemConfiguration activeConfig = createTestSystemConfiguration(
                "active.config",
                "true",
                "BOOLEAN",
                "Configuración activa",
                null);
        activeConfig.setActive(true);
        repository.save(activeConfig);

        SystemConfiguration inactiveConfig = createTestSystemConfiguration(
                "inactive.config",
                "false",
                "BOOLEAN",
                "Configuración inactiva",
                null);
        inactiveConfig.setActive(false);
        repository.save(inactiveConfig);

        // Buscar todas las configuraciones y filtrar las activas
        List<SystemConfiguration> allConfigs = repository.findAll();
        List<SystemConfiguration> activeConfigs = allConfigs.stream()
                .filter(SystemConfiguration::isActive)
                .toList();

        assertEquals(1, activeConfigs.size());
        assertEquals("active.config", activeConfigs.get(0).getConfigKey());
    }

    @Test
    void updateSystemConfiguration() {
        // Crear configuración
        SystemConfiguration config = createTestSystemConfiguration(
                "test.config",
                "original_value",
                "STRING",
                "Configuración de prueba",
                null);
        SystemConfiguration savedConfig = repository.save(config);

        // Actualizar
        savedConfig.setConfigValue("updated_value");
        savedConfig.setDescription("Configuración actualizada");
        savedConfig.setUpdatedAt(LocalDateTime.now().plusSeconds(1)); // Asegurar que updatedAt sea posterior
        SystemConfiguration updatedConfig = repository.save(savedConfig);

        // Verificar
        assertEquals("updated_value", updatedConfig.getConfigValue());
        assertEquals("Configuración actualizada", updatedConfig.getDescription());
        assertTrue(updatedConfig.getUpdatedAt().isAfter(updatedConfig.getCreatedAt()));
    }

    @Test
    void multipleConfigurations() {
        // Crear múltiples configuraciones
        SystemConfiguration config1 = createTestSystemConfiguration(
                "config1",
                "value1",
                "STRING",
                "Configuración 1",
                null);
        repository.save(config1);

        SystemConfiguration config2 = createTestSystemConfiguration(
                "config2",
                "value2",
                "INT",
                "Configuración 2",
                null);
        repository.save(config2);

        SystemConfiguration config3 = createTestSystemConfiguration(
                "config3",
                "value3",
                "BOOLEAN",
                "Configuración 3",
                null);
        repository.save(config3);

        // Verificar que todas se guardaron
        List<SystemConfiguration> allConfigs = repository.findAll();
        assertEquals(3, allConfigs.size());

        // Verificar que se pueden encontrar individualmente
        Optional<SystemConfiguration> found1 = repository.findByConfigKey("config1");
        Optional<SystemConfiguration> found2 = repository.findByConfigKey("config2");
        Optional<SystemConfiguration> found3 = repository.findByConfigKey("config3");

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertTrue(found3.isPresent());
        assertEquals("STRING", found1.get().getDataType());
        assertEquals("INT", found2.get().getDataType());
        assertEquals("BOOLEAN", found3.get().getDataType());
    }

    @Test
    void configurationWithUser() {
        // Crear usuario administrador
        User admin = createTestAdmin();
        User savedAdmin = userRepository.save(admin);

        // Crear configuración con usuario
        SystemConfiguration config = createTestSystemConfiguration(
                "user.updated.config",
                "user_value",
                "STRING",
                "Configuración actualizada por usuario",
                savedAdmin);
        SystemConfiguration savedConfig = repository.save(config);

        // Verificar relación con usuario
        assertNotNull(savedConfig.getUpdatedBy());
        assertEquals(savedAdmin.getId(), savedConfig.getUpdatedBy().getId());
        assertEquals("Admin de Prueba", savedConfig.getUpdatedBy().getName());
    }

    // Método auxiliar para crear datos de prueba
    private User createTestAdmin() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setEmail("admin@cooperativa.com");
        user.setIdentificationNumber("87654321");
        user.setName("Admin de Prueba");
        user.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        user.setRole(User.UserRole.ADMIN);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private SystemConfiguration createTestSystemConfiguration(String key, String value, String dataType,
            String description, User updatedBy) {
        LocalDateTime now = LocalDateTime.now();
        SystemConfiguration config = new SystemConfiguration(key, value);
        config.setDataType(dataType);
        config.setDescription(description);
        config.setUpdatedBy(updatedBy);
        config.setCreatedAt(now);
        config.setUpdatedAt(now);
        return config;
    }
}

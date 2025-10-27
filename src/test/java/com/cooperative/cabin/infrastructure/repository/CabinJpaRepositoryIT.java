package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.TestAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class CabinJpaRepositoryIT {

    @Resource
    private CabinJpaRepository repository;

    @Test
    void saveAndFindCabin() {
        // Crear cabaña usando constructor moderno
        Cabin cabin = new Cabin(
                "Cabaña del Lago",
                "Hermosa cabaña frente al lago con vista panorámica",
                6,
                3,
                2,
                new BigDecimal("150.00"),
                8,
                "[\"WiFi\", \"Parking\", \"Chimenea\"]",
                "{\"address\": \"Lago Escondido 123\", \"coordinates\": {\"lat\": -34.6037, \"lng\": -58.3816}}",
                LocalTime.of(15, 0), // Check-in 15:00
                LocalTime.of(11, 0) // Check-out 11:00
        );

        // Establecer campos de auditoría manualmente
        LocalDateTime now = LocalDateTime.now();
        cabin.setCreatedAt(now);
        cabin.setUpdatedAt(now);

        // Guardar
        Cabin savedCabin = repository.save(cabin);

        // Verificar
        assertNotNull(savedCabin.getId());
        assertEquals("Cabaña del Lago", savedCabin.getName());
        assertEquals(6, savedCabin.getCapacity());
        assertEquals(new BigDecimal("150.00"), savedCabin.getBasePrice());
        assertTrue(savedCabin.getActive());
        assertNotNull(savedCabin.getCreatedAt());
        assertNotNull(savedCabin.getUpdatedAt());
    }

    @Test
    void findActiveCabins() {
        LocalDateTime now = LocalDateTime.now();

        // Crear cabañas activas e inactivas
        Cabin activeCabin = new Cabin("Cabaña Activa", "Descripción", 4, 2, 1,
                new BigDecimal("100.00"), 4, "{}", "{}", LocalTime.of(15, 0), LocalTime.of(11, 0));
        activeCabin.setActive(true);
        activeCabin.setCreatedAt(now);
        activeCabin.setUpdatedAt(now);
        repository.save(activeCabin);

        Cabin inactiveCabin = new Cabin("Cabaña Inactiva", "Descripción", 4, 2, 1,
                new BigDecimal("100.00"), 4, "{}", "{}", LocalTime.of(15, 0), LocalTime.of(11, 0));
        inactiveCabin.setActive(false);
        inactiveCabin.setCreatedAt(now);
        inactiveCabin.setUpdatedAt(now);
        repository.save(inactiveCabin);

        // Buscar solo activas
        List<Cabin> activeCabins = repository.findByActiveTrue();

        assertEquals(1, activeCabins.size());
        assertEquals("Cabaña Activa", activeCabins.get(0).getName());
    }

    @Test
    void updateCabin() {
        LocalDateTime now = LocalDateTime.now();

        // Crear cabaña
        Cabin cabin = new Cabin("Cabaña Original", "Descripción original", 4, 2, 1,
                new BigDecimal("100.00"), 4, "{}", "{}", LocalTime.of(15, 0), LocalTime.of(11, 0));
        cabin.setCreatedAt(now);
        cabin.setUpdatedAt(now);
        Cabin savedCabin = repository.save(cabin);

        // Actualizar
        savedCabin.setName("Cabaña Actualizada");
        savedCabin.setBasePrice(new BigDecimal("120.00"));
        savedCabin.setUpdatedAt(LocalDateTime.now().plusSeconds(1)); // Asegurar que updatedAt sea posterior
        Cabin updatedCabin = repository.save(savedCabin);

        // Verificar
        assertEquals("Cabaña Actualizada", updatedCabin.getName());
        assertEquals(new BigDecimal("120.00"), updatedCabin.getBasePrice());
        assertTrue(updatedCabin.getUpdatedAt().isAfter(updatedCabin.getCreatedAt()));
    }
}

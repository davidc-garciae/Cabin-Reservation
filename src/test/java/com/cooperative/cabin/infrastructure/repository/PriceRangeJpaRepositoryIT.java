package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.cooperative.cabin.TestAuditingConfiguration;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class PriceRangeJpaRepositoryIT {

    @Resource
    private PriceRangeJpaRepository repository;

    @Resource
    private CabinJpaRepository cabinRepository;

    @Resource
    private UserJpaRepository userRepository;

    @Test
    void saveAndFindPriceRange() {
        // Crear entidades relacionadas
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();

        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear rango de precios usando método auxiliar
        PriceRange priceRange = createTestPriceRange(
                savedCabin,
                savedAdmin,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31),
                new BigDecimal("150.00"),
                new BigDecimal("1.5"), // 50% de incremento
                "Temporada alta de verano");

        // Guardar
        PriceRange savedPriceRange = repository.save(priceRange);

        // Verificar
        assertNotNull(savedPriceRange.getId());
        assertEquals(savedCabin.getId(), savedPriceRange.getCabin().getId());
        assertEquals(LocalDate.of(2025, 1, 1), savedPriceRange.getStartDate());
        assertEquals(LocalDate.of(2025, 1, 31), savedPriceRange.getEndDate());
        assertEquals(new BigDecimal("150.00"), savedPriceRange.getBasePrice());
        assertEquals(new BigDecimal("1.5"), savedPriceRange.getPriceMultiplier());
        assertEquals("Temporada alta de verano", savedPriceRange.getReason());
        assertEquals(savedAdmin.getId(), savedPriceRange.getCreatedBy().getId());
        assertNotNull(savedPriceRange.getCreatedAt());
        assertNotNull(savedPriceRange.getUpdatedAt());
    }

    @Test
    void findByCabin_Id() {
        // Crear datos de prueba
        Cabin cabin1 = createTestCabin();
        cabin1.setName("Cabaña 1");
        Cabin cabin2 = createTestCabin();
        cabin2.setName("Cabaña 2");
        User admin = createTestAdmin();

        Cabin savedCabin1 = cabinRepository.save(cabin1);
        Cabin savedCabin2 = cabinRepository.save(cabin2);
        User savedAdmin = userRepository.save(admin);

        // Crear rangos de precios para diferentes cabañas
        PriceRange range1 = createTestPriceRange(savedCabin1, savedAdmin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), new BigDecimal("1.2"), "Temporada alta");
        repository.save(range1);

        PriceRange range2 = createTestPriceRange(savedCabin2, savedAdmin,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28),
                new BigDecimal("120.00"), new BigDecimal("1.3"), "Temporada media");
        repository.save(range2);

        // Buscar rangos por cabaña
        List<PriceRange> cabin1Ranges = repository.findByCabin_Id(savedCabin1.getId());
        List<PriceRange> cabin2Ranges = repository.findByCabin_Id(savedCabin2.getId());

        assertEquals(1, cabin1Ranges.size());
        assertEquals(1, cabin2Ranges.size());
        assertEquals("Temporada alta", cabin1Ranges.get(0).getReason());
        assertEquals("Temporada media", cabin2Ranges.get(0).getReason());
    }

    @Test
    void findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual() {
        // Crear datos de prueba
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();
        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear rangos de precios con diferentes fechas
        PriceRange januaryRange = createTestPriceRange(savedCabin, savedAdmin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), new BigDecimal("1.2"), "Enero");
        repository.save(januaryRange);

        PriceRange februaryRange = createTestPriceRange(savedCabin, savedAdmin,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28),
                new BigDecimal("120.00"), new BigDecimal("1.3"), "Febrero");
        repository.save(februaryRange);

        PriceRange marchRange = createTestPriceRange(savedCabin, savedAdmin,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31),
                new BigDecimal("110.00"), new BigDecimal("1.1"), "Marzo");
        repository.save(marchRange);

        // Buscar rango que aplique para el 15 de enero
        List<PriceRange> applicableRanges = repository
                .findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        savedCabin.getId(),
                        LocalDate.of(2025, 1, 15),
                        LocalDate.of(2025, 1, 15));

        assertEquals(1, applicableRanges.size());
        assertEquals("Enero", applicableRanges.get(0).getReason());
        assertEquals(new BigDecimal("1.2"), applicableRanges.get(0).getPriceMultiplier());
    }

    @Test
    void updatePriceRange() {
        // Crear datos de prueba
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();
        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear rango de precios
        PriceRange priceRange = createTestPriceRange(savedCabin, savedAdmin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), new BigDecimal("1.2"), "Precio original");
        PriceRange savedPriceRange = repository.save(priceRange);

        // Actualizar
        savedPriceRange.setBasePrice(new BigDecimal("110.00"));
        savedPriceRange.setPriceMultiplier(new BigDecimal("1.3"));
        savedPriceRange.setReason("Precio actualizado");
        savedPriceRange.setUpdatedAt(LocalDateTime.now().plusSeconds(1)); // Asegurar que updatedAt sea posterior
        PriceRange updatedPriceRange = repository.save(savedPriceRange);

        // Verificar
        assertEquals(new BigDecimal("110.00"), updatedPriceRange.getBasePrice());
        assertEquals(new BigDecimal("1.3"), updatedPriceRange.getPriceMultiplier());
        assertEquals("Precio actualizado", updatedPriceRange.getReason());
        assertTrue(updatedPriceRange.getUpdatedAt().isAfter(updatedPriceRange.getCreatedAt()));
    }

    // Métodos auxiliares para crear datos de prueba
    private Cabin createTestCabin() {
        LocalDateTime now = LocalDateTime.now();
        Cabin cabin = new Cabin(
                "Cabaña de Prueba",
                "Cabaña para tests de precios",
                4,
                2,
                1,
                new BigDecimal("100.00"),
                4,
                "{\"wifi\": true}",
                "{\"address\": \"Test Location\"}");
        cabin.setCreatedAt(now);
        cabin.setUpdatedAt(now);
        return cabin;
    }

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

    private PriceRange createTestPriceRange(Cabin cabin, User admin, LocalDate startDate, LocalDate endDate,
            BigDecimal basePrice, BigDecimal multiplier, String reason) {
        LocalDateTime now = LocalDateTime.now();
        PriceRange priceRange = new PriceRange(cabin, startDate, endDate, basePrice, multiplier, reason, admin);
        priceRange.setCreatedAt(now);
        priceRange.setUpdatedAt(now);
        return priceRange;
    }
}

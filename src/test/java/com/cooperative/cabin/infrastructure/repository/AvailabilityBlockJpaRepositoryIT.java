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
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class AvailabilityBlockJpaRepositoryIT {

    @Resource
    private AvailabilityBlockJpaRepository repository;

    @Resource
    private CabinJpaRepository cabinRepository;

    @Resource
    private UserJpaRepository userRepository;

    @Test
    void saveAndFindAvailabilityBlock() {
        // Crear entidades relacionadas
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();

        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear bloque de disponibilidad usando método auxiliar
        AvailabilityBlock block = createTestAvailabilityBlock(
                savedCabin,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 7),
                "Mantenimiento programado",
                savedAdmin);

        // Guardar
        AvailabilityBlock savedBlock = repository.save(block);

        // Verificar
        assertNotNull(savedBlock.getId());
        assertEquals(savedCabin.getId(), savedBlock.getCabin().getId());
        assertEquals(LocalDate.of(2025, 3, 1), savedBlock.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 7), savedBlock.getEndDate());
        assertEquals("Mantenimiento programado", savedBlock.getReason());
        assertEquals(savedAdmin.getId(), savedBlock.getCreatedBy().getId());
        assertNotNull(savedBlock.getCreatedAt());
        assertNotNull(savedBlock.getUpdatedAt());
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

        // Crear bloques para diferentes cabañas
        AvailabilityBlock block1 = createTestAvailabilityBlock(savedCabin1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3),
                "Mantenimiento Cabaña 1", savedAdmin);
        repository.save(block1);

        AvailabilityBlock block2 = createTestAvailabilityBlock(savedCabin2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 5),
                "Mantenimiento Cabaña 2", savedAdmin);
        repository.save(block2);

        // Buscar bloques por cabaña
        List<AvailabilityBlock> cabin1Blocks = repository.findByCabin_Id(savedCabin1.getId());
        List<AvailabilityBlock> cabin2Blocks = repository.findByCabin_Id(savedCabin2.getId());

        assertEquals(1, cabin1Blocks.size());
        assertEquals(1, cabin2Blocks.size());
        assertEquals("Mantenimiento Cabaña 1", cabin1Blocks.get(0).getReason());
        assertEquals("Mantenimiento Cabaña 2", cabin2Blocks.get(0).getReason());
    }

    @Test
    void multipleBlocksForSameCabin() {
        // Crear datos de prueba
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();
        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear múltiples bloques para la misma cabaña
        AvailabilityBlock block1 = createTestAvailabilityBlock(savedCabin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3),
                "Mantenimiento enero", savedAdmin);
        repository.save(block1);

        AvailabilityBlock block2 = createTestAvailabilityBlock(savedCabin,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 5),
                "Mantenimiento febrero", savedAdmin);
        repository.save(block2);

        AvailabilityBlock block3 = createTestAvailabilityBlock(savedCabin,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 7),
                "Mantenimiento marzo", savedAdmin);
        repository.save(block3);

        // Buscar todos los bloques de la cabaña
        List<AvailabilityBlock> allBlocks = repository.findByCabin_Id(savedCabin.getId());

        assertEquals(3, allBlocks.size());

        // Verificar que todos los bloques pertenecen a la misma cabaña
        allBlocks.forEach(block -> {
            assertEquals(savedCabin.getId(), block.getCabin().getId());
        });
    }

    @Test
    void updateAvailabilityBlock() {
        // Crear datos de prueba
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();
        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear bloque de disponibilidad
        AvailabilityBlock block = createTestAvailabilityBlock(savedCabin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3),
                "Mantenimiento original", savedAdmin);
        AvailabilityBlock savedBlock = repository.save(block);

        // Actualizar
        savedBlock.setStartDate(LocalDate.of(2025, 1, 2));
        savedBlock.setEndDate(LocalDate.of(2025, 1, 4));
        savedBlock.setReason("Mantenimiento extendido");
        savedBlock.setUpdatedAt(LocalDateTime.now().plusSeconds(1)); // Asegurar que updatedAt sea posterior
        AvailabilityBlock updatedBlock = repository.save(savedBlock);

        // Verificar
        assertEquals(LocalDate.of(2025, 1, 2), updatedBlock.getStartDate());
        assertEquals(LocalDate.of(2025, 1, 4), updatedBlock.getEndDate());
        assertEquals("Mantenimiento extendido", updatedBlock.getReason());
        assertTrue(updatedBlock.getUpdatedAt().isAfter(updatedBlock.getCreatedAt()));
    }

    @Test
    void deleteAvailabilityBlock() {
        // Crear datos de prueba
        Cabin cabin = createTestCabin();
        User admin = createTestAdmin();
        Cabin savedCabin = cabinRepository.save(cabin);
        User savedAdmin = userRepository.save(admin);

        // Crear bloque de disponibilidad
        AvailabilityBlock block = createTestAvailabilityBlock(savedCabin,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3),
                "Mantenimiento temporal", savedAdmin);
        AvailabilityBlock savedBlock = repository.save(block);

        // Verificar que existe
        List<AvailabilityBlock> blocks = repository.findByCabin_Id(savedCabin.getId());
        assertEquals(1, blocks.size());

        // Eliminar
        repository.delete(savedBlock);

        // Verificar que se eliminó
        List<AvailabilityBlock> remainingBlocks = repository.findByCabin_Id(savedCabin.getId());
        assertEquals(0, remainingBlocks.size());
    }

    // Métodos auxiliares para crear datos de prueba
    private Cabin createTestCabin() {
        LocalDateTime now = LocalDateTime.now();
        Cabin cabin = new Cabin(
                "Cabaña de Prueba",
                "Cabaña para tests de disponibilidad",
                4,
                2,
                1,
                new BigDecimal("100.00"),
                4,
                "{\"wifi\": true}",
                "{\"address\": \"Test Location\"}",
                LocalTime.of(15, 0), // Check-in 15:00
                LocalTime.of(11, 0) // Check-out 11:00
        );
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

    private AvailabilityBlock createTestAvailabilityBlock(Cabin cabin, LocalDate startDate, LocalDate endDate,
            String reason, User createdBy) {
        LocalDateTime now = LocalDateTime.now();
        AvailabilityBlock block = new AvailabilityBlock(cabin, startDate, endDate, reason, createdBy);
        block.setCreatedAt(now);
        block.setUpdatedAt(now);
        return block;
    }
}

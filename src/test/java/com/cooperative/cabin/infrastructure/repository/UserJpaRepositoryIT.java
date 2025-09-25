package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.cooperative.cabin.TestAuditingConfiguration;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class UserJpaRepositoryIT {

    @Resource
    private UserJpaRepository repository;

    @Test
    void saveAndFindUser() {
        LocalDateTime now = LocalDateTime.now();

        // Crear usuario usando constructor moderno
        User user = new User();
        user.setEmail("profesor@cooperativa.com");
        user.setIdentificationNumber("12345678");
        user.setName("Juan Pérez");
        user.setPhone("+54 11 1234-5678");
        user.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        user.setRole(User.UserRole.PROFESSOR);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Guardar
        User savedUser = repository.save(user);

        // Verificar
        assertNotNull(savedUser.getId());
        assertEquals("profesor@cooperativa.com", savedUser.getEmail());
        assertEquals("12345678", savedUser.getIdentificationNumber());
        assertEquals("Juan Pérez", savedUser.getName());
        assertEquals(User.UserRole.PROFESSOR, savedUser.getRole());
        assertTrue(savedUser.getActive());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void findByEmail() {
        LocalDateTime now = LocalDateTime.now();

        // Crear usuario
        User user = new User();
        user.setEmail("admin@cooperativa.com");
        user.setIdentificationNumber("87654321");
        user.setName("Admin Sistema");
        user.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        user.setRole(User.UserRole.ADMIN);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        repository.save(user);

        // Buscar por email
        Optional<User> found = repository.findByEmail("admin@cooperativa.com");

        assertTrue(found.isPresent());
        assertEquals("Admin Sistema", found.get().getName());
        assertEquals(User.UserRole.ADMIN, found.get().getRole());
    }

    @Test
    void findByIdentificationNumber() {
        LocalDateTime now = LocalDateTime.now();

        // Crear usuario
        User user = new User();
        user.setEmail("jubilado@cooperativa.com");
        user.setIdentificationNumber("11223344");
        user.setName("María González");
        user.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        user.setRole(User.UserRole.RETIREE);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        repository.save(user);

        // Buscar por número de identificación
        Optional<User> found = repository.findByIdentificationNumber("11223344");

        assertTrue(found.isPresent());
        assertEquals("María González", found.get().getName());
        assertEquals(User.UserRole.RETIREE, found.get().getRole());
    }

    @Test
    void findByActiveTrue() {
        LocalDateTime now = LocalDateTime.now();

        // Crear usuarios activos e inactivos
        User activeUser = new User();
        activeUser.setEmail("activo@cooperativa.com");
        activeUser.setIdentificationNumber("11111111");
        activeUser.setName("Usuario Activo");
        activeUser.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        activeUser.setRole(User.UserRole.PROFESSOR);
        activeUser.setActive(true);
        activeUser.setCreatedAt(now);
        activeUser.setUpdatedAt(now);
        repository.save(activeUser);

        User inactiveUser = new User();
        inactiveUser.setEmail("inactivo@cooperativa.com");
        inactiveUser.setIdentificationNumber("22222222");
        inactiveUser.setName("Usuario Inactivo");
        inactiveUser.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        inactiveUser.setRole(User.UserRole.PROFESSOR);
        inactiveUser.setActive(false);
        inactiveUser.setCreatedAt(now);
        inactiveUser.setUpdatedAt(now);
        repository.save(inactiveUser);

        // Buscar solo activos
        List<User> activeUsers = repository.findByActiveTrue(PageRequest.of(0, 10)).getContent();

        assertEquals(1, activeUsers.size());
        assertEquals("Usuario Activo", activeUsers.get(0).getName());
    }
}

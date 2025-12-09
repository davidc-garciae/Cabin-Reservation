package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.exception.UserNotFoundException;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminUserApplicationServiceImplTest {

    private UserJpaRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AdminUserApplicationServiceImpl service;

    @BeforeEach
    void setup() {
        userRepository = mock(UserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new AdminUserApplicationServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        var resp = service.upsertUser(null, "a@test.com", "123", "User A", "ADMIN", true);

        assertThat(resp.getId()).isEqualTo(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldPromoteUserAndFlagPasswordChange() {
        User existing = new User();
        existing.setId(5L);
        existing.setRole(User.UserRole.PROFESSOR);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.upsertUser(5L, "a@test.com", "123", "User A", "ADMIN", true);

        assertThat(existing.getMustChangePassword()).isTrue();
    }

    @Test
    void shouldListUsersByEmail() {
        User user = new User();
        user.setId(2L);
        user.setEmail("a@test.com");
        user.setIdentificationNumber("123");
        user.setName("A");
        user.setRole(User.UserRole.ADMIN);
        user.setActive(true);
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));

        List<?> result = service.listUsers("a@test.com", null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldDeleteExistingUser() {
        when(userRepository.existsById(3L)).thenReturn(true);
        service.deleteUser(3L);
        verify(userRepository).deleteById(3L);
    }

    @Test
    void shouldFailDeleteWhenMissing() {
        when(userRepository.existsById(3L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteUser(3L)).isInstanceOf(UserNotFoundException.class);
    }
}


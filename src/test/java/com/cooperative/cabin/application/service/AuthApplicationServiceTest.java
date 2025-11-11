package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.DocumentNumberJpaRepository;
import com.cooperative.cabin.infrastructure.repository.PasswordResetTokenRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthApplicationServiceTest {

    private AuthApplicationService authApplicationService;
    private JwtService jwtService;
    private UserJpaRepository userRepository;
    private DocumentNumberJpaRepository documentNumberRepository;
    private PasswordResetTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        jwtService = Mockito.mock(JwtService.class);
        userRepository = Mockito.mock(UserJpaRepository.class);
        documentNumberRepository = Mockito.mock(DocumentNumberJpaRepository.class);
        tokenRepository = Mockito.mock(PasswordResetTokenRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        authApplicationService = new AuthApplicationService(
                jwtService, userRepository, documentNumberRepository, tokenRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Debería registrar usuario exitosamente cuando documento es válido")
    void shouldRegisterUserSuccessfullyWhenDocumentIsValid() {
        // Given
        String documentNumber = "12345678";
        String email = "test@email.com";
        String name = "Test User";
        String phone = "+57-300-123-4567";
        String pin = "1234";
        String role = "PROFESSOR";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(pin)).thenReturn("encoded_pin");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setRole(User.UserRole.PROFESSOR);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtService.generateAccessToken(email, "PROFESSOR", 1L)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(email)).thenReturn("refresh_token");

        // When
        Map<String, String> result = authApplicationService.register(documentNumber, email, name, phone, pin, role);

        // Then
        assertThat(result).containsEntry("accessToken", "access_token");
        assertThat(result).containsEntry("refreshToken", "refresh_token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debería fallar cuando documento no es válido")
    void shouldFailWhenDocumentIsNotValid() {
        // Given
        String documentNumber = "invalid_doc";
        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(false);

        // When & Then
        assertThatThrownBy(
                () -> authApplicationService.register(documentNumber, "test@email.com", "Test", null, "1234",
                        "PROFESSOR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Número de documento no válido o deshabilitado");
    }

    @Test
    @DisplayName("Debería fallar cuando documento ya tiene usuario registrado")
    void shouldFailWhenDocumentAlreadyHasUser() {
        // Given
        String documentNumber = "12345678";
        String email = "test@email.com";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(true);

        // When & Then
        assertThatThrownBy(
                () -> authApplicationService.register(documentNumber, email, "Test", null, "1234", "PROFESSOR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ya existe un usuario con este número de documento");
    }

    @Test
    @DisplayName("Debería fallar cuando email ya está registrado")
    void shouldFailWhenEmailAlreadyExists() {
        // Given
        String documentNumber = "12345678";
        String email = "existing@email.com";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(
                () -> authApplicationService.register(documentNumber, email, "Test", null, "1234", "PROFESSOR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ya existe un usuario con este email");
    }

    @Test
    @DisplayName("Debería generar tokens JWT tras registro exitoso")
    void shouldGenerateJwtTokensAfterSuccessfulRegistration() {
        // Given
        String documentNumber = "12345678";
        String email = "test@email.com";
        String name = "Test User";
        String phone = "+57-300-123-4567";
        String pin = "1234";
        String role = "PROFESSOR";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(pin)).thenReturn("encoded_pin");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setRole(User.UserRole.PROFESSOR);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtService.generateAccessToken(email, "PROFESSOR", 1L)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(email)).thenReturn("refresh_token");

        // When
        Map<String, String> result = authApplicationService.register(documentNumber, email, name, phone, pin, role);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsKey("accessToken");
        assertThat(result).containsKey("refreshToken");
        verify(jwtService).generateAccessToken(email, "PROFESSOR", 1L);
        verify(jwtService).generateRefreshToken(email);
    }

    @Test
    @DisplayName("Debería fallar cuando se intenta registrar con rol ADMIN")
    void shouldFailWhenTryingToRegisterWithAdminRole() {
        // Given
        String documentNumber = "12345678";
        String email = "test@email.com";
        String name = "Test User";
        String phone = "+57-300-123-4567";
        String pin = "1234";
        String role = "ADMIN";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authApplicationService.register(documentNumber, email, name, phone, pin, role))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede registrar un usuario con rol ADMIN");
    }

    @Test
    @DisplayName("Debería fallar cuando el rol es inválido")
    void shouldFailWhenRoleIsInvalid() {
        // Given
        String documentNumber = "12345678";
        String email = "test@email.com";
        String name = "Test User";
        String phone = "+57-300-123-4567";
        String pin = "1234";
        String role = "INVALID_ROLE";

        when(documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)).thenReturn(true);
        when(userRepository.existsByIdentificationNumber(documentNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authApplicationService.register(documentNumber, email, name, phone, pin, role))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rol inválido. Solo se permiten PROFESSOR o RETIREE");
    }
}

package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.DocumentNumber;
import com.cooperative.cabin.domain.model.PasswordResetToken;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.DocumentNumberJpaRepository;
import com.cooperative.cabin.infrastructure.repository.PasswordResetTokenRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthApplicationService {

    private final JwtService jwtService;
    private final UserJpaRepository userRepository;
    private final DocumentNumberJpaRepository documentNumberRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(JwtService jwtService,
            UserJpaRepository userRepository,
            DocumentNumberJpaRepository documentNumberRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.documentNumberRepository = documentNumberRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> login(String documentNumber, String password) {
        // 1. Verificar que el número de documento existe y está activo
        if (!documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)) {
            throw new IllegalArgumentException("Número de documento no válido o deshabilitado");
        }

        // 2. Buscar el usuario por número de documento
        Optional<User> userOpt = userRepository.findByIdentificationNumber(documentNumber);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado para este número de documento");
        }

        User user = userOpt.get();

        // 3. Verificar que el usuario está activo
        if (!user.getActive()) {
            throw new IllegalArgumentException("Usuario deshabilitado");
        }

        // 4. Verificar la contraseña
        if (!passwordEncoder.matches(password, user.getPinHash())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        // 5. Generar tokens JWT
        String access = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        return Map.of("accessToken", access, "refreshToken", refresh);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        String newAccess = jwtService.refreshAccessToken(refreshToken);
        return Map.of("accessToken", newAccess);
    }

    public void recoverPassword(String documentNumber) {
        // Verificar que el número de documento existe y está activo
        if (!documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)) {
            // Por seguridad, no revelamos si el número de documento existe o no
            return;
        }

        // Buscar el usuario por número de documento
        Optional<User> userOpt = userRepository.findByIdentificationNumber(documentNumber);
        if (userOpt.isEmpty()) {
            // Por seguridad, no revelamos si el usuario existe o no
            return;
        }

        User user = userOpt.get();
        String email = user.getEmail();

        // Marcar tokens anteriores como usados
        tokenRepository.markAllTokensAsUsedForEmail(email);

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 horas

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiresAt);
        tokenRepository.save(resetToken);

        // TODO: IMPLEMENTAR SERVICIO DE NOTIFICACIONES POR EMAIL
        //
        // REQUERIMIENTOS:
        // 1. Crear NotificationService interface y implementación
        // 2. Integrar con proveedor de email (SendGrid, AWS SES, etc.)
        // 3. Crear template de email para password reset
        // 4. Configurar variables de entorno para credenciales de email
        // 5. Manejar errores de envío de email
        // 6. Agregar logs de envío exitoso/fallido
        // 7. Considerar cola de emails para envío asíncrono
        //
        // IMPLEMENTACIÓN SUGERIDA:
        // notificationService.sendPasswordResetEmail(email, token, documentNumber);
        //
        // Por ahora, solo logueamos el token para testing
        // System.out.println("Password reset token for " + email + " (document: " +
        // documentNumber + "): " + token);
    }

    public void resetPassword(String token, String newPin) {
        // Buscar el token
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token inválido");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Verificar que el token es válido
        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Token expirado o ya utilizado");
        }

        // Buscar el usuario
        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        User user = userOpt.get();

        // Actualizar el PIN
        String hashedPin = passwordEncoder.encode(newPin);
        user.setPinHash(hashedPin);
        userRepository.save(user);

        // Marcar el token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public Map<String, String> register(String documentNumber, String email, String name, String phone, String pin,
            String role) {
        // 1. Validar que el documento existe y está activo (REUTILIZAR lógica
        // existente)
        if (!documentNumberRepository.existsByDocumentNumberAndActive(documentNumber)) {
            throw new IllegalArgumentException("Número de documento no válido o deshabilitado");
        }

        // 2. Verificar que no existe usuario con este documento (NUEVO)
        if (userRepository.existsByIdentificationNumber(documentNumber)) {
            throw new IllegalArgumentException("Ya existe un usuario con este número de documento");
        }

        // 3. Verificar que no existe usuario con este email (NUEVO)
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }

        // 4. Crear nuevo usuario (REUTILIZAR lógica de AdminUserApplicationServiceImpl)
        User user = new User();
        user.setEmail(email);
        user.setIdentificationNumber(documentNumber);
        user.setName(name);
        user.setPhone(phone);
        user.setPinHash(passwordEncoder.encode(pin));

        // Convertir string a enum y validar
        User.UserRole userRole;
        try {
            userRole = User.UserRole.valueOf(role);
            if (userRole == User.UserRole.ADMIN) {
                throw new IllegalArgumentException("No se puede registrar un usuario con rol ADMIN");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido. Solo se permiten PROFESSOR o RETIREE");
        }

        user.setRole(userRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // 5. Generar tokens JWT (REUTILIZAR lógica existente)
        String access = jwtService.generateAccessToken(savedUser.getEmail(), savedUser.getRole().name());
        String refresh = jwtService.generateRefreshToken(savedUser.getEmail());

        return Map.of("accessToken", access, "refreshToken", refresh);
    }
}

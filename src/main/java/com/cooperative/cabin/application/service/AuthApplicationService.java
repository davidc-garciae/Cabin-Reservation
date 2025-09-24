package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.PasswordResetToken;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.PasswordResetTokenRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthApplicationService {

    private final JwtService jwtService;
    private final UserJpaRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(JwtService jwtService,
            UserJpaRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> login(String username, String password) {
        // Verificar credenciales (implementación básica para testing)
        // En producción, esto debería verificar contra la base de datos
        String role = "admin".equalsIgnoreCase(username) ? "ADMIN" : "USER";
        String access = jwtService.generateAccessToken(username, role);
        String refresh = jwtService.generateRefreshToken(username);
        return Map.of("accessToken", access, "refreshToken", refresh);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        String newAccess = jwtService.refreshAccessToken(refreshToken);
        return Map.of("accessToken", newAccess);
    }

    public void recoverPassword(String email) {
        // Verificar que el usuario existe
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Por seguridad, no revelamos si el email existe o no
            return;
        }

        // Marcar tokens anteriores como usados
        tokenRepository.markAllTokensAsUsedForEmail(email);

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(24L * 60 * 60); // 24 horas

        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiresAt);
        tokenRepository.save(resetToken);

        // TODO: Enviar email con el token (implementar servicio de notificaciones)
        // Por ahora, solo logueamos el token para testing
        // System.out.println("Password reset token for " + email + ": " + token);
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
        tokenRepository.deleteExpiredTokens(Instant.now());
    }
}

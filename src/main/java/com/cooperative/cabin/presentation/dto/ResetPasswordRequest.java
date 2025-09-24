package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de restablecimiento de contraseña")
public record ResetPasswordRequest(
        @Schema(description = "Token de recuperación", example = "abc123def456") @NotBlank(message = "El token es obligatorio") String token,

        @Schema(description = "Nuevo PIN de 4 dígitos", example = "1234") @NotBlank(message = "El PIN es obligatorio") @Pattern(regexp = "\\d{4}", message = "El PIN debe tener exactamente 4 dígitos") String newPin) {
}


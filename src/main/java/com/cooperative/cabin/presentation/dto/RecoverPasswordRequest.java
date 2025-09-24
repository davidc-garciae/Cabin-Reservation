package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de recuperación de contraseña")
public record RecoverPasswordRequest(
        @Schema(description = "Email del usuario", example = "usuario@ejemplo.com") @NotBlank(message = "El email es obligatorio") @Email(message = "El formato del email no es válido") String email) {
}


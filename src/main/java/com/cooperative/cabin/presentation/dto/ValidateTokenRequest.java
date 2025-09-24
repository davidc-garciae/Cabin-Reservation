package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de validación de token")
public record ValidateTokenRequest(
        @Schema(description = "Token a validar", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") @NotBlank(message = "El token es obligatorio") String token) {
}


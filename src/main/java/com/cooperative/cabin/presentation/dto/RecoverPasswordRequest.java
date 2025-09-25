package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de recuperación de contraseña")
public record RecoverPasswordRequest(
        @Schema(description = "Número de documento del usuario", example = "12345678") @NotBlank(message = "El número de documento es obligatorio") @Size(min = 8, max = 20, message = "El número de documento debe tener entre 8 y 20 caracteres") @Pattern(regexp = "^\\d+$", message = "El número de documento solo puede contener dígitos") String documentNumber) {
}

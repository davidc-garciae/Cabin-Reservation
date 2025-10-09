package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de número de documento")
public record DocumentNumberResponse(
        @Schema(description = "ID único del documento", example = "1") Long id,
        @Schema(description = "Número de documento", example = "12345678") String documentNumber,
        @Schema(description = "Estado del documento", example = "ACTIVE") String status,
        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00") LocalDateTime createdAt,
        @Schema(description = "Fecha de última actualización", example = "2024-01-20T14:45:00") LocalDateTime updatedAt) {
}

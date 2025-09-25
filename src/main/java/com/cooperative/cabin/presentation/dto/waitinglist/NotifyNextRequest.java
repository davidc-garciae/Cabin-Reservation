package com.cooperative.cabin.presentation.dto.waitinglist;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record NotifyNextRequest(
        @Schema(description = "ID de la cabaña liberada", example = "1") Long cabinId,
        @Schema(description = "Fecha de inicio del hueco", example = "2025-03-10") LocalDate startDate,
        @Schema(description = "Fecha de fin del hueco", example = "2025-03-12") LocalDate endDate,
        @Schema(description = "Ventana de oportunidad en horas (por defecto 4)", example = "4") Integer windowHours) {
}

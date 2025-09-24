package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen general del sistema para el panel de administración")
public record AdminDashboardResponse(
        @Schema(description = "Total de reservas registradas", example = "124") long reservationsTotal,
        @Schema(description = "Reservas en estado PENDING", example = "8") long reservationsPending,
        @Schema(description = "Reservas en estado CONFIRMED", example = "32") long reservationsConfirmed,
        @Schema(description = "Reservas en estado IN_USE", example = "5") long reservationsInUse,
        @Schema(description = "Reservas en estado COMPLETED", example = "70") long reservationsCompleted,
        @Schema(description = "Reservas en estado CANCELLED", example = "9") long reservationsCancelled,
        @Schema(description = "Usuarios activos", example = "340") long activeUsers,
        @Schema(description = "Cabañas activas", example = "5") long activeCabins,
        @Schema(description = "Ocupación estimada actual (0-100)", example = "65") int occupancyRatePercent,
        @Schema(description = "Reservas creadas (métrica)", example = "12") Integer reservationsCreated,
        @Schema(description = "Reservas canceladas (métrica)", example = "3") Integer reservationsCancelledMetric,
        @Schema(description = "Transiciones de estado (métrica)", example = "15") Integer statusTransitions,
        @Schema(description = "Transiciones por scheduler (métrica)", example = "6") Integer schedulerTransitions) {
}

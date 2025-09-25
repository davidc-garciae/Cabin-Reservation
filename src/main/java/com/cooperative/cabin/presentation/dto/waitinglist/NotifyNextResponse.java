package com.cooperative.cabin.presentation.dto.waitinglist;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record NotifyNextResponse(
        @Schema(description = "ID del ítem en lista de espera", example = "10") Long waitingListId,
        @Schema(description = "ID del usuario notificado", example = "5") Long userId,
        @Schema(description = "ID de la cabaña", example = "1") Long cabinId,
        @Schema(description = "Token de notificación para claim", example = "b4b7a3d5-1a3c-4a2b-bb89-2ca6b0f3e6fa") String notifyToken,
        @Schema(description = "Fecha/hora de expiración del token", example = "2025-03-09T12:00:00") LocalDateTime notifyExpiresAt) {
}

package com.cooperative.cabin.presentation.dto.waitinglist;

import io.swagger.v3.oas.annotations.media.Schema;

public record ClaimRequest(
        @Schema(description = "Token de notificación", example = "b4b7a3d5-1a3c-4a2b-bb89-2ca6b0f3e6fa") String notifyToken,
        @Schema(description = "Número de huéspedes a confirmar", example = "2") Integer numberOfGuests) {
}

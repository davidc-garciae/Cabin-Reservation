package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Información de una cabaña")
public record CabinResponse(
        @Schema(description = "ID único de la cabaña", example = "1") Long id,

        @Schema(description = "Nombre de la cabaña", example = "Cabaña del Lago") String name,

        @Schema(description = "Descripción detallada de la cabaña", example = "Hermosa cabaña con vista al lago, ideal para familias") String description,

        @Schema(description = "Capacidad máxima de huéspedes", example = "6") Integer capacity,

        @Schema(description = "Número de habitaciones", example = "3") Integer bedrooms,

        @Schema(description = "Número de baños", example = "2") Integer bathrooms,

        @Schema(description = "Precio base por noche", example = "150.00") BigDecimal basePrice,

        @Schema(description = "Máximo de huéspedes permitidos", example = "6") Integer maxGuests,

        @Schema(description = "Estado activo de la cabaña", example = "true") Boolean active,

        @Schema(description = "Comodidades disponibles (JSON)", example = "{\"wifi\": true, \"parking\": true, \"kitchen\": true}") String amenities,

        @Schema(description = "Ubicación (JSON con coordenadas y dirección)", example = "{\"address\": \"Lago del Sol 123\", \"coordinates\": {\"lat\": 40.7128, \"lng\": -74.0060}}") String location,

        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00Z") Instant createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-20T14:45:00Z") Instant updatedAt) {
}

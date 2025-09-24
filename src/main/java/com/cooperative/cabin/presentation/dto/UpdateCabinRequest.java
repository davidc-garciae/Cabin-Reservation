package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Datos para actualizar una cabaña existente")
public record UpdateCabinRequest(
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres") @Schema(description = "Nombre de la cabaña", example = "Cabaña del Lago Renovada") String name,

        @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres") @Schema(description = "Descripción detallada de la cabaña", example = "Hermosa cabaña renovada con vista al lago, ideal para familias") String description,

        @Min(value = 1, message = "La capacidad debe ser al menos 1") @Max(value = 20, message = "La capacidad no puede exceder 20") @Schema(description = "Capacidad máxima de huéspedes", example = "8") Integer capacity,

        @Min(value = 1, message = "Debe tener al menos 1 habitación") @Max(value = 10, message = "No puede tener más de 10 habitaciones") @Schema(description = "Número de habitaciones", example = "4") Integer bedrooms,

        @Min(value = 1, message = "Debe tener al menos 1 baño") @Max(value = 5, message = "No puede tener más de 5 baños") @Schema(description = "Número de baños", example = "3") Integer bathrooms,

        @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0") @DecimalMax(value = "9999.99", message = "El precio base no puede exceder 9999.99") @Schema(description = "Precio base por noche", example = "180.00") BigDecimal basePrice,

        @Min(value = 1, message = "El máximo de huéspedes debe ser al menos 1") @Max(value = 20, message = "El máximo de huéspedes no puede exceder 20") @Schema(description = "Máximo de huéspedes permitidos", example = "8") Integer maxGuests,

        @Schema(description = "Estado activo de la cabaña", example = "true") Boolean active,

        @Schema(description = "Comodidades disponibles (JSON)", example = "{\"wifi\": true, \"parking\": true, \"kitchen\": true, \"pool\": true}") String amenities,

        @Schema(description = "Ubicación (JSON con coordenadas y dirección)", example = "{\"address\": \"Lago del Sol 123\", \"coordinates\": {\"lat\": 40.7128, \"lng\": -74.0060}}") String location) {
}

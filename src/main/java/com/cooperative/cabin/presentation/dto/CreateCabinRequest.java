package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Datos para crear una nueva cabaña")
public record CreateCabinRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede exceder 100 caracteres") @Schema(description = "Nombre de la cabaña", example = "Cabaña del Lago", required = true) String name,

        @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres") @Schema(description = "Descripción detallada de la cabaña", example = "Hermosa cabaña con vista al lago, ideal para familias") String description,

        @NotNull(message = "La capacidad es obligatoria") @Min(value = 1, message = "La capacidad debe ser al menos 1") @Max(value = 20, message = "La capacidad no puede exceder 20") @Schema(description = "Capacidad máxima de huéspedes", example = "6", required = true) Integer capacity,

        @NotNull(message = "El número de habitaciones es obligatorio") @Min(value = 1, message = "Debe tener al menos 1 habitación") @Max(value = 10, message = "No puede tener más de 10 habitaciones") @Schema(description = "Número de habitaciones", example = "3", required = true) Integer bedrooms,

        @NotNull(message = "El número de baños es obligatorio") @Min(value = 1, message = "Debe tener al menos 1 baño") @Max(value = 5, message = "No puede tener más de 5 baños") @Schema(description = "Número de baños", example = "2", required = true) Integer bathrooms,

        @NotNull(message = "El precio base es obligatorio") @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0") @DecimalMax(value = "9999.99", message = "El precio base no puede exceder 9999.99") @Schema(description = "Precio base por noche", example = "150.00", required = true) BigDecimal basePrice,

        @NotNull(message = "El máximo de huéspedes es obligatorio") @Min(value = 1, message = "El máximo de huéspedes debe ser al menos 1") @Max(value = 20, message = "El máximo de huéspedes no puede exceder 20") @Schema(description = "Máximo de huéspedes permitidos", example = "6", required = true) Integer maxGuests,

        @Schema(description = "Comodidades disponibles (JSON)", example = "{\"wifi\": true, \"parking\": true, \"kitchen\": true}") String amenities,

        @Schema(description = "Ubicación (JSON con coordenadas y dirección)", example = "{\"address\": \"Lago del Sol 123\", \"coordinates\": {\"lat\": 40.7128, \"lng\": -74.0060}}") String location) {
}

package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Solicitud de actualización de número de documento")
public record UpdateDocumentNumberRequest(
        @Pattern(regexp = "^(ACTIVE|DISABLED)$", message = "El estado debe ser ACTIVE o DISABLED") @Schema(description = "Estado del documento", example = "DISABLED", allowableValues = {
                "ACTIVE", "DISABLED" }) String status){
}

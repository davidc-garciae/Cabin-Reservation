package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de creación de número de documento")
public record CreateDocumentNumberRequest(
        @NotBlank(message = "El número de documento es obligatorio") @Size(min = 7, max = 20, message = "El número de documento debe tener entre 7 y 20 caracteres") @Pattern(regexp = "^\\d+$", message = "El número de documento solo puede contener dígitos") @Schema(description = "Número de documento", example = "12345678", required = true) String documentNumber,

        @Schema(description = "Estado del documento", example = "ACTIVE", allowableValues = {
                "ACTIVE", "DISABLED" }) String status){
    public CreateDocumentNumberRequest {
        if (status == null) {
            status = "ACTIVE";
        }
    }
}

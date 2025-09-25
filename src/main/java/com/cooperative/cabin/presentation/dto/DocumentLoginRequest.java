package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de login con número de documento")
public class DocumentLoginRequest {

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 20, message = "El número de documento debe tener entre 8 y 20 caracteres")
    @Pattern(regexp = "^\\d+$", message = "El número de documento solo puede contener dígitos")
    @Schema(description = "Número de documento del usuario", example = "12345678", required = true)
    private String documentNumber;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;

    // Constructors
    public DocumentLoginRequest() {
    }

    public DocumentLoginRequest(String documentNumber, String password) {
        this.documentNumber = documentNumber;
        this.password = password;
    }

    // Getters and Setters
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DocumentLoginRequest{" +
                "documentNumber='" + documentNumber + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}

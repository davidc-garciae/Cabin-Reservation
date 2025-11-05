package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de cambio de contraseña/PIN")
public class ChangePasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    @Schema(description = "Contraseña o PIN actual del usuario", example = "1234", required = true)
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 4, max = 50, message = "La nueva contraseña debe tener entre 4 y 50 caracteres")
    @Schema(description = "Nueva contraseña o PIN (PIN de 4 dígitos para usuarios normales, contraseña de 6-50 caracteres para administradores)", example = "SecurePass123", required = true)
    private String newPassword;

    // Constructors
    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                '}';
    }
}

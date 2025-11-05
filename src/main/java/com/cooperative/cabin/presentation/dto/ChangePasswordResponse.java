package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de cambio de contraseña exitoso")
public class ChangePasswordResponse {

    @Schema(description = "Mensaje de confirmación", example = "Contraseña actualizada exitosamente")
    private String message;

    @Schema(description = "Indica si el usuario debe cambiar su contraseña", example = "false")
    private Boolean mustChangePassword;

    // Constructors
    public ChangePasswordResponse() {
    }

    public ChangePasswordResponse(String message, Boolean mustChangePassword) {
        this.message = message;
        this.mustChangePassword = mustChangePassword;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}

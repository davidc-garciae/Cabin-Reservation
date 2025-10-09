package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Solicitud de registro de nuevo usuario")
public class RegisterRequest {

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 7, max = 20, message = "El número de documento debe tener entre 7 y 20 caracteres")
    @Pattern(regexp = "^\\d+$", message = "El número de documento solo puede contener dígitos")
    @Schema(description = "Número de documento del usuario", example = "12345678", required = true)
    private String documentNumber;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email debe tener un formato válido")
    @Size(max = 255, message = "Email no puede exceder 255 caracteres")
    @Schema(description = "Email del usuario", example = "usuario@email.com", required = true)
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", required = true)
    private String name;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[\\d\\s\\-()]+$", message = "Teléfono debe tener un formato válido")
    @Schema(description = "Número de teléfono", example = "+57-300-123-4567", required = false)
    private String phone;

    @NotBlank(message = "El PIN es obligatorio")
    @Size(min = 4, max = 4, message = "El PIN debe tener exactamente 4 dígitos")
    @Pattern(regexp = "^\\d{4}$", message = "El PIN debe contener solo 4 dígitos")
    @Schema(description = "PIN de 4 dígitos", example = "1234", required = true)
    private String pin;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(PROFESSOR|RETIREE)$", message = "El rol debe ser PROFESSOR o RETIREE")
    @Schema(description = "Rol del usuario", example = "PROFESSOR", allowableValues = { "PROFESSOR",
            "RETIREE" }, required = true)
    private String role;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String documentNumber, String email, String name, String phone, String pin, String role) {
        this.documentNumber = documentNumber;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.pin = pin;
        this.role = role;
    }

    // Getters and Setters
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

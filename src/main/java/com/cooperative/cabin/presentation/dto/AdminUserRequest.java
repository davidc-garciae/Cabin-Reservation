package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de upsert de usuario admin")
public class AdminUserRequest {
    @Schema(example = "john.doe@example.com")
    private String email;

    @Schema(example = "John Doe")
    private String fullName;

    @Schema(example = "ADMIN")
    private String role;

    @Schema(example = "true")
    private boolean active;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

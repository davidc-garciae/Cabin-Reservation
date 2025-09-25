package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Usuario (vista admin)")
public class AdminUserResponse {
    private Long id;
    private String email;
    private String documentNumber;
    private String fullName;
    private String role;
    private boolean active;

    public AdminUserResponse(Long id, String email, String documentNumber, String fullName, String role,
            boolean active) {
        this.id = id;
        this.email = email;
        this.documentNumber = documentNumber;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}

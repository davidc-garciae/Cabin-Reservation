package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_identification_number", columnList = "identification_number"),
        @Index(name = "idx_user_active", columnList = "active"),
        @Index(name = "idx_user_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email debe tener un formato válido")
    @NotBlank(message = "Email es obligatorio")
    @Size(max = 255, message = "Email no puede exceder 255 caracteres")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Número de identificación es obligatorio")
    @Size(min = 7, max = 20, message = "Número de identificación debe tener entre 7 y 20 caracteres")
    @Pattern(regexp = "^\\d+$", message = "Número de identificación debe contener solo dígitos")
    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String name;

    @Size(max = 20, message = "Teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[\\d\\s\\-()]+$", message = "Teléfono debe tener un formato válido")
    @Column
    private String phone;

    @NotBlank(message = "Hash de PIN es obligatorio")
    @Column(nullable = false)
    private String pinHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = false;

    // Relaciones bidireccionales
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceRange> createdPriceRanges = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AvailabilityBlock> createdAvailabilityBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SystemConfiguration> updatedSystemConfigurations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WaitingList> waitingListEntries = new ArrayList<>();

    @OneToMany(mappedBy = "changedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceChangeHistory> priceChangeHistories = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum UserRole {
        ADMIN, PROFESSOR, RETIREE
    }
}

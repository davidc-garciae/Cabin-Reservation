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
import java.time.LocalDate;

@Entity
@Table(name = "availability_blocks", indexes = {
        @Index(name = "idx_availability_block_cabin_id", columnList = "cabin_id"),
        @Index(name = "idx_availability_block_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_availability_block_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AvailabilityBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cabaña es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabin_id", nullable = false)
    private Cabin cabin;

    @NotNull(message = "Fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Fecha de fin es obligatoria")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Size(max = 500, message = "Razón no puede exceder 500 caracteres")
    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public AvailabilityBlock(Cabin cabin, LocalDate startDate, LocalDate endDate, String reason, User createdBy) {
        this.cabin = cabin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.createdBy = createdBy;
    }

    // Constructor de compatibilidad para tests (DEPRECATED - solo para tests)
    @Deprecated
    public AvailabilityBlock(Long id, Long cabinId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        // Crear entidad mock para tests
        this.cabin = new Cabin();
        this.cabin.setId(cabinId);
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = "Test data";
        this.createdBy = null;
    }

    // Convenience method for backward compatibility
    public Long getCabinId() {
        return cabin != null ? cabin.getId() : null;
    }
}

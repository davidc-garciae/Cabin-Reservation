package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "price_ranges", indexes = {
        @Index(name = "idx_price_range_cabin_id", columnList = "cabin_id"),
        @Index(name = "idx_price_range_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_price_range_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PriceRange {
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

    @NotNull(message = "Precio base es obligatorio")
    @DecimalMin(value = "0.01", message = "Precio base debe ser mayor a 0")
    @DecimalMax(value = "9999.99", message = "Precio base no puede exceder 9999.99")
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @NotNull(message = "Multiplicador de precio es obligatorio")
    @DecimalMin(value = "0.01", message = "Multiplicador debe ser mayor a 0")
    @DecimalMax(value = "10.00", message = "Multiplicador no puede exceder 10.00")
    @Column(name = "price_multiplier", nullable = false)
    private BigDecimal priceMultiplier; // 1.00 means no change

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Relaciones bidireccionales
    @OneToMany(mappedBy = "priceRange", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceChangeHistory> priceChangeHistories = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public PriceRange(Cabin cabin, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
            BigDecimal priceMultiplier, String reason, User createdBy) {
        this.cabin = cabin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePrice = basePrice;
        this.priceMultiplier = priceMultiplier;
        this.reason = reason;
        this.createdBy = createdBy;
    }

}

package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "price_ranges")
public class PriceRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cabin_id", nullable = false)
    private Long cabinId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "price_multiplier", nullable = false)
    private BigDecimal priceMultiplier; // 1.00 means no change

    public PriceRange(Long id, Long cabinId, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
            BigDecimal priceMultiplier) {
        this.id = id;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePrice = basePrice;
        this.priceMultiplier = priceMultiplier;
    }

    public Long getCabinId() {
        return cabinId;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }
}

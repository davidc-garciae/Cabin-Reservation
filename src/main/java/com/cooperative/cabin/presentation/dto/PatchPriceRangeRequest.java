package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PatchPriceRangeRequest {
    @Schema(example = "150.00")
    private BigDecimal basePrice;
    @Schema(example = "1.10")
    private BigDecimal multiplier;
    @Schema(example = "2025-03-01")
    private LocalDate startDate;
    @Schema(example = "2025-03-31")
    private LocalDate endDate;

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

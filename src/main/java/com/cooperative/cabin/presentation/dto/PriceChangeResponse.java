package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cambio de precio")
public class PriceChangeResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "2")
    private Long cabinId;
    @Schema(example = "2025-02-01")
    private String date;
    @Schema(example = "110.00")
    private String oldPrice;
    @Schema(example = "120.00")
    private String newPrice;

    public PriceChangeResponse() {
    }

    public PriceChangeResponse(Long id, Long cabinId, String date, String oldPrice, String newPrice) {
        this.id = id;
        this.cabinId = cabinId;
        this.date = date;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getCabinId() {
        return cabinId;
    }

    public String getDate() {
        return date;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }
}



package com.cooperative.cabin.presentation.dto;

public class PriceRangeResponse {
    private Long id;
    private Long cabinId;
    private String startDate;
    private String endDate;
    private String basePrice;
    private String multiplier;

    public Long getId() {
        return id;
    }

    public Long getCabinId() {
        return cabinId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public String getMultiplier() {
        return multiplier;
    }
}

package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Disponibilidad por día")
public class AvailabilityDayResponse {
    @Schema(example = "2025-02-01")
    private String date;

    @Schema(description = "Indica si hay disponibilidad", example = "true")
    private boolean available;

    public AvailabilityDayResponse() {
    }

    public AvailabilityDayResponse(String date, boolean available) {
        this.date = date;
        this.available = available;
    }

    public String getDate() {
        return date;
    }

    public boolean isAvailable() {
        return available;
    }
}



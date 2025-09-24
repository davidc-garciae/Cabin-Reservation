package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reserva")
public class ReservationResponse {
    @Schema(example = "10")
    private Long id;
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "2")
    private Long cabinId;
    @Schema(example = "2025-01-10")
    private String startDate;
    @Schema(example = "2025-01-12")
    private String endDate;
    @Schema(example = "2")
    private int guests;
    @Schema(example = "PENDING")
    private String status;

    public ReservationResponse() {
    }

    public ReservationResponse(Long id, Long userId, Long cabinId, String startDate, String endDate, int guests,
            String status) {
        this.id = id;
        this.userId = userId;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guests = guests;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public int getGuests() {
        return guests;
    }

    public String getStatus() {
        return status;
    }
}

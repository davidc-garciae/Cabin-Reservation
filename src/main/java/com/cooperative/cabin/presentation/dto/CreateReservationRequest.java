package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de creación de pre-reserva")
public class CreateReservationRequest {
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

    @Schema(example = "14:00", description = "Hora de check-in")
    private String checkInTime;

    @Schema(example = "12:00", description = "Hora de check-out")
    private String checkOutTime;

    public CreateReservationRequest() {
    }

    public CreateReservationRequest(Long userId, Long cabinId, String startDate, String endDate, int guests) {
        this.userId = userId;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guests = guests;
    }

    public CreateReservationRequest(Long userId, Long cabinId, String startDate, String endDate, int guests,
            String checkInTime, String checkOutTime) {
        this.userId = userId;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guests = guests;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
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

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}

package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

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

    @Schema(example = "14:00", description = "Hora de check-in")
    private String checkInTime;

    @Schema(example = "12:00", description = "Hora de check-out")
    private String checkOutTime;

    @Schema(example = "150000.00", description = "Precio base de la reserva")
    private BigDecimal basePrice;

    @Schema(example = "180000.00", description = "Precio final de la reserva")
    private BigDecimal finalPrice;

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

    public ReservationResponse(Long id, Long userId, Long cabinId, String startDate, String endDate, int guests,
            String status, String checkInTime, String checkOutTime) {
        this.id = id;
        this.userId = userId;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guests = guests;
        this.status = status;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
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

    // Setters para MapStruct
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCabinId(Long cabinId) {
        this.cabinId = cabinId;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
}

package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Solicitud de creación de pre-reserva")
public class CreateReservationRequest {
    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(example = "1", required = true)
    private Long userId;

    @NotNull(message = "El ID de la cabaña es obligatorio")
    @Schema(example = "2", required = true)
    private Long cabinId;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "La fecha de inicio debe tener el formato YYYY-MM-DD")
    @Schema(example = "2025-01-10", required = true)
    private String startDate;

    @NotBlank(message = "La fecha de fin es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "La fecha de fin debe tener el formato YYYY-MM-DD")
    @Schema(example = "2025-01-12", required = true)
    private String endDate;

    @Min(value = 1, message = "El número de huéspedes debe ser al menos 1")
    @Schema(example = "2", required = true)
    private int guests;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora de check-in debe tener el formato HH:mm")
    @Schema(example = "14:00", description = "Hora de check-in (formato HH:mm)")
    private String checkInTime;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora de check-out debe tener el formato HH:mm")
    @Schema(example = "12:00", description = "Hora de check-out (formato HH:mm)")
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

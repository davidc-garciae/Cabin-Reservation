package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bloqueo de disponibilidad")
public class BlockResponse {
    @Schema(example = "10")
    private Long id;
    @Schema(example = "2")
    private Long cabinId;
    @Schema(example = "2025-05-01")
    private String startDate;
    @Schema(example = "2025-05-03")
    private String endDate;

    public BlockResponse() {
    }

    public BlockResponse(Long id, Long cabinId, String startDate, String endDate) {
        this.id = id;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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
}



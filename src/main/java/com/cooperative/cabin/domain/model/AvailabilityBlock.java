package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "availability_blocks")
public class AvailabilityBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cabin_id", nullable = false)
    private Long cabinId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public AvailabilityBlock(Long id, Long cabinId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    protected AvailabilityBlock() {
    }

    public Long getId() {
        return id;
    }

    public Long getCabinId() {
        return cabinId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

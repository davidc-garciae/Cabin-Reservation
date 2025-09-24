package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityBlocksAdminService {
    List<AvailabilityBlock> list();

    AvailabilityBlock create(Long cabinId, LocalDate startDate, LocalDate endDate);

    AvailabilityBlock update(Long id, Long cabinId, LocalDate startDate, LocalDate endDate);

    void delete(Long id);
}

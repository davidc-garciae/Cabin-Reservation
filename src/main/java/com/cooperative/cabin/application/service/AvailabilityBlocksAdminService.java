package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityBlocksAdminService {
    List<AvailabilityBlock> list();

    AvailabilityBlock create(Long cabinId, LocalDate startDate, LocalDate endDate, Long userId);

    AvailabilityBlock update(Long id, Long cabinId, LocalDate startDate, LocalDate endDate, Long userId);

    void delete(Long id);
}

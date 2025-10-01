package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.presentation.dto.BlockResponse;

public class AvailabilityBlockMapper {
    public static BlockResponse toResponse(AvailabilityBlock b) {
        if (b == null)
            return null;
        return new BlockResponse(b.getId(), b.getCabin().getId(), b.getStartDate().toString(),
                b.getEndDate().toString());
    }
}

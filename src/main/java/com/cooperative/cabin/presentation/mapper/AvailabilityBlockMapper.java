package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.presentation.dto.BlockResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AvailabilityBlockMapper {

    AvailabilityBlockMapper INSTANCE = Mappers.getMapper(AvailabilityBlockMapper.class);

    @Mapping(source = "cabin.id", target = "cabinId")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd")
    BlockResponse toResponse(AvailabilityBlock b);
}

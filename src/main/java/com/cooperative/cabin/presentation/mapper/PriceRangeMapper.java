package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.presentation.dto.PriceRangeResponse;
import com.cooperative.cabin.presentation.controller.PricingAdminController.CreatePriceRangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PriceRangeMapper {

    PriceRangeMapper INSTANCE = Mappers.getMapper(PriceRangeMapper.class);

    @Mapping(source = "cabin.id", target = "cabinId")
    @Mapping(source = "basePrice", target = "basePrice", numberFormat = "#0.00")
    @Mapping(source = "priceMultiplier", target = "multiplier", numberFormat = "#0.00")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd")
    PriceRangeResponse toResponse(PriceRange pr);

    @Mapping(target = "cabin", expression = "java(createCabinFromId(req.getCabinId()))")
    @Mapping(source = "multiplier", target = "priceMultiplier")
    @Mapping(target = "reason", constant = "Created via API")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "priceChangeHistories", ignore = true)
    PriceRange fromCreateRequest(CreatePriceRangeRequest req);
    
    default com.cooperative.cabin.domain.model.Cabin createCabinFromId(Long cabinId) {
        if (cabinId == null) {
            return null;
        }
        com.cooperative.cabin.domain.model.Cabin cabin = new com.cooperative.cabin.domain.model.Cabin();
        cabin.setId(cabinId);
        return cabin;
    }
}

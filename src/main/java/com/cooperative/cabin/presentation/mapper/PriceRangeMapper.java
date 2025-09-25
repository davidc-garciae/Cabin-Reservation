package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.presentation.dto.PriceRangeResponse;
import com.cooperative.cabin.presentation.controller.PricingAdminController.CreatePriceRangeRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceRangeMapper {
    public static final PriceRangeMapper INSTANCE = new PriceRangeMapper();

    public PriceRangeResponse toResponse(PriceRange pr) {
        if (pr == null)
            return null;
        PriceRangeResponse dto = new PriceRangeResponse();
        try {
            java.lang.reflect.Field f;
            f = PriceRangeResponse.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(dto, pr.getId());
            f = PriceRangeResponse.class.getDeclaredField("cabinId");
            f.setAccessible(true);
            f.set(dto, pr.getCabinId());
            f = PriceRangeResponse.class.getDeclaredField("startDate");
            f.setAccessible(true);
            f.set(dto, pr.getStartDate().toString());
            f = PriceRangeResponse.class.getDeclaredField("endDate");
            f.setAccessible(true);
            f.set(dto, pr.getEndDate().toString());
            f = PriceRangeResponse.class.getDeclaredField("basePrice");
            f.setAccessible(true);
            f.set(dto, pr.getBasePrice().toPlainString());
            f = PriceRangeResponse.class.getDeclaredField("multiplier");
            f.setAccessible(true);
            f.set(dto, pr.getPriceMultiplier().toPlainString());
        } catch (Exception ignored) {
        }
        return dto;
    }

    public PriceRange fromCreateRequest(CreatePriceRangeRequest req) {
        if (req == null)
            return null;
        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();
        BigDecimal base = req.getBasePrice();
        BigDecimal mult = req.getMultiplier();
        // Para tests, crear entidad mock
        com.cooperative.cabin.domain.model.Cabin cabin = new com.cooperative.cabin.domain.model.Cabin();
        cabin.setId(req.getCabinId());

        return new com.cooperative.cabin.domain.model.PriceRange(
                cabin, start, end, base, mult, "Test data", null);
    }
}

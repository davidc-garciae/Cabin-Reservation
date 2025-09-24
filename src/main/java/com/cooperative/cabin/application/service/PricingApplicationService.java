package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.PriceRange;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PricingApplicationService {
        PriceRange partialUpdatePriceRange(Long id, BigDecimal basePrice, BigDecimal multiplier,
                        LocalDate startDate, LocalDate endDate);

        java.util.List<PriceRange> listPriceRanges();

        PriceRange createPriceRange(Long cabinId, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
                        BigDecimal multiplier);

        PriceRange updatePriceRange(Long id, Long cabinId, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
                        BigDecimal multiplier);

        void deletePriceRange(Long id);

        java.util.Map<String, BigDecimal> getCalendar(int year, int month);

        java.util.List<java.util.Map<String, Object>> getHistory();

        BigDecimal calculatePrice(Long cabinId, LocalDate date);
}

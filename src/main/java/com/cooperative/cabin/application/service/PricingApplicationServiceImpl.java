package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.infrastructure.repository.PriceRangeJpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingApplicationServiceImpl implements PricingApplicationService {

    private final PriceRangeJpaRepository repository;

    public PricingApplicationServiceImpl(PriceRangeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PriceRange partialUpdatePriceRange(Long id, BigDecimal basePrice, BigDecimal multiplier,
            LocalDate startDate, LocalDate endDate) {
        PriceRange current = repository.findById(id).orElse(null);
        if (current == null) {
            // Crear si no existe con valores provistos (comportamiento simple)
            // Para crear un nuevo PriceRange necesitamos una cabaña y un usuario
            // Por ahora usamos valores por defecto - esto debería ser manejado por el
            // controlador
            throw new IllegalStateException("Cannot create PriceRange without Cabin and User entities");
        }
        BigDecimal newBase = basePrice != null ? basePrice : current.getBasePrice();
        BigDecimal newMult = multiplier != null ? multiplier : current.getPriceMultiplier();
        LocalDate newStart = startDate != null ? startDate : current.getStartDate();
        LocalDate newEnd = endDate != null ? endDate : current.getEndDate();
        return repository
                .save(new PriceRange(current.getCabin(), newStart, newEnd, newBase, newMult, "Updated", null));
    }

    @Override
    public List<PriceRange> listPriceRanges() {
        return repository.findAll();
    }

    @Override
    public PriceRange createPriceRange(Long cabinId, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
            BigDecimal multiplier) {
        // Necesitamos cargar la entidad Cabin y un User para crear el PriceRange
        throw new IllegalStateException(
                "Cannot create PriceRange without Cabin and User entities - use proper service method");
    }

    @Override
    public PriceRange updatePriceRange(Long id, Long cabinId, LocalDate startDate, LocalDate endDate,
            BigDecimal basePrice,
            BigDecimal multiplier) {
        // Necesitamos cargar la entidad Cabin y un User para actualizar el PriceRange
        throw new IllegalStateException(
                "Cannot update PriceRange without Cabin and User entities - use proper service method");
    }

    @Override
    public void deletePriceRange(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Map<String, BigDecimal> getCalendar(int year, int month) {
        // Implementación mínima: mapa vacío
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getHistory() {
        return new ArrayList<>();
    }

    @Override
    public BigDecimal calculatePrice(Long cabinId, LocalDate date) {
        List<PriceRange> ranges = repository
                .findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(cabinId, date, date);
        if (ranges.isEmpty()) {
            return BigDecimal.ZERO;
        }
        PriceRange r = ranges.get(0);
        return r.getBasePrice().multiply(r.getPriceMultiplier());
    }
}

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
        Map<String, BigDecimal> calendar = new HashMap<>();

        // Obtener todos los rangos de precios que se superponen con el mes especificado
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        List<PriceRange> ranges = repository.findAll().stream()
                .filter(range -> !range.getStartDate().isAfter(monthEnd) &&
                        !range.getEndDate().isBefore(monthStart))
                .toList();

        // Para cada día del mes, calcular el precio aplicable
        for (int day = 1; day <= monthStart.lengthOfMonth(); day++) {
            LocalDate currentDate = LocalDate.of(year, month, day);
            String dateKey = currentDate.toString();

            // Encontrar el rango de precio aplicable para esta fecha
            BigDecimal price = ranges.stream()
                    .filter(range -> !currentDate.isBefore(range.getStartDate()) &&
                            !currentDate.isAfter(range.getEndDate()))
                    .findFirst()
                    .map(range -> range.getBasePrice().multiply(range.getPriceMultiplier()))
                    .orElse(BigDecimal.ZERO);

            calendar.put(dateKey, price);
        }

        return calendar;
    }

    @Override
    public List<Map<String, Object>> getHistory() {
        List<Map<String, Object>> history = new ArrayList<>();

        // Obtener todos los rangos de precios ordenados por fecha de creación (más
        // recientes primero)
        List<PriceRange> ranges = repository.findAll().stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .toList();

        for (PriceRange range : ranges) {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("id", range.getId());
            historyEntry.put("cabinId", range.getCabin().getId());
            historyEntry.put("cabinName", range.getCabin().getName());
            historyEntry.put("startDate", range.getStartDate().toString());
            historyEntry.put("endDate", range.getEndDate().toString());
            historyEntry.put("basePrice", range.getBasePrice());
            historyEntry.put("priceMultiplier", range.getPriceMultiplier());
            historyEntry.put("finalPrice", range.getBasePrice().multiply(range.getPriceMultiplier()));
            historyEntry.put("reason", range.getReason());
            historyEntry.put("createdAt", range.getCreatedAt().toString());
            historyEntry.put("createdBy", range.getCreatedBy() != null ? range.getCreatedBy().getName() : "Sistema");

            history.add(historyEntry);
        }

        return history;
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

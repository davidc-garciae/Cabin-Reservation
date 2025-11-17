package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.PriceRangeJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingApplicationServiceImpl implements PricingApplicationService {

    private final PriceRangeJpaRepository repository;
    private final CabinJpaRepository cabinRepository;
    private final UserJpaRepository userRepository;

    public PricingApplicationServiceImpl(
            PriceRangeJpaRepository repository,
            CabinJpaRepository cabinRepository,
            UserJpaRepository userRepository) {
        this.repository = repository;
        this.cabinRepository = cabinRepository;
        this.userRepository = userRepository;
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
    @Transactional
    public PriceRange createPriceRange(Long cabinId, LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
            BigDecimal multiplier, Long userId) {
        // Cargar entidades necesarias
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new IllegalArgumentException("Cabin not found with id: " + cabinId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Validar fechas
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Validar precios (verificar null primero)
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price is required");
        }
        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0");
        }
        if (multiplier == null) {
            throw new IllegalArgumentException("Multiplier is required");
        }
        if (multiplier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Multiplier must be greater than 0");
        }

        // Crear el rango de precios usando constructor sin argumentos para evitar problemas con auditoría
        PriceRange range = new PriceRange();
        range.setCabin(cabin);
        range.setStartDate(startDate);
        range.setEndDate(endDate);
        range.setBasePrice(basePrice);
        range.setPriceMultiplier(multiplier);
        range.setReason("Created by admin");
        range.setCreatedBy(user);
        return repository.save(range);
    }

    @Override
    @Transactional
    public PriceRange updatePriceRange(Long id, Long cabinId, LocalDate startDate, LocalDate endDate,
            BigDecimal basePrice,
            BigDecimal multiplier) {
        // Buscar el rango existente
        PriceRange existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Price range not found with id: " + id));

        // Cargar entidad Cabin si cambió
        Cabin cabin = existing.getCabin();
        if (!existing.getCabin().getId().equals(cabinId)) {
            cabin = cabinRepository.findById(cabinId)
                    .orElseThrow(() -> new IllegalArgumentException("Cabin not found with id: " + cabinId));
        }

        // Validar fechas
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Validar precios
        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0");
        }
        if (multiplier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Multiplier must be greater than 0");
        }

        // Actualizar campos (usando setters de Lombok @Data)
        existing.setCabin(cabin);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        existing.setBasePrice(basePrice);
        existing.setPriceMultiplier(multiplier);
        existing.setReason("Updated by admin");

        return repository.save(existing);
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
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHistory() {
        List<Map<String, Object>> history = new ArrayList<>();

        // Obtener todos los rangos de precios ordenados por fecha de creación (más
        // recientes primero)
        // Cargar las relaciones lazy dentro de la transacción
        List<PriceRange> ranges = repository.findAll();
        
        // Forzar la carga de relaciones lazy antes de salir del método transaccional
        ranges.forEach(range -> {
            if (range.getCabin() != null) {
                range.getCabin().getName(); // Forzar inicialización
            }
            if (range.getCreatedBy() != null) {
                range.getCreatedBy().getName(); // Forzar inicialización
            }
        });
        
        List<PriceRange> sortedRanges = ranges.stream()
                .sorted((r1, r2) -> {
                    if (r1.getCreatedAt() == null && r2.getCreatedAt() == null) return 0;
                    if (r1.getCreatedAt() == null) return 1; // null va al final
                    if (r2.getCreatedAt() == null) return -1; // null va al final
                    return r2.getCreatedAt().compareTo(r1.getCreatedAt());
                })
                .toList();

        for (PriceRange range : sortedRanges) {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("id", range.getId());
            historyEntry.put("cabinId", range.getCabin() != null ? range.getCabin().getId() : null);
            historyEntry.put("cabinName", range.getCabin() != null ? range.getCabin().getName() : "N/A");
            historyEntry.put("startDate", range.getStartDate() != null ? range.getStartDate().toString() : null);
            historyEntry.put("endDate", range.getEndDate() != null ? range.getEndDate().toString() : null);
            historyEntry.put("basePrice", range.getBasePrice());
            historyEntry.put("priceMultiplier", range.getPriceMultiplier());
            historyEntry.put("finalPrice", range.getBasePrice() != null && range.getPriceMultiplier() != null 
                    ? range.getBasePrice().multiply(range.getPriceMultiplier()) : null);
            historyEntry.put("reason", range.getReason());
            historyEntry.put("createdAt", range.getCreatedAt() != null ? range.getCreatedAt().toString() : null);
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

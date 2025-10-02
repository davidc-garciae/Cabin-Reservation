package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.policy.AvailabilityPolicies;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AvailabilityApplicationServiceImpl implements AvailabilityApplicationService {

    private final ReservationJpaRepository reservationRepository;
    private final AvailabilityBlockJpaRepository availabilityBlockRepository;
    private final CabinJpaRepository cabinRepository;

    public AvailabilityApplicationServiceImpl(
            ReservationJpaRepository reservationRepository,
            AvailabilityBlockJpaRepository availabilityBlockRepository,
            CabinJpaRepository cabinRepository) {
        this.reservationRepository = reservationRepository;
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.cabinRepository = cabinRepository;
    }

    // Métodos existentes convertidos a lógica real
    @Override
    public List<String> getAvailableDates() {
        // Obtener todas las cabañas activas
        List<Cabin> activeCabins = cabinRepository.findByActiveTrue();

        if (activeCabins.isEmpty()) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(3); // Próximos 3 meses

        // Obtener fechas disponibles de todas las cabañas activas
        return activeCabins.stream()
                .flatMap(cabin -> getAvailableDatesInRange(cabin.getId(), today, endDate).stream())
                .distinct() // Eliminar duplicados
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Boolean> getAvailabilityCalendar(int year, int month) {
        // Obtener todas las cabañas activas
        List<Cabin> activeCabins = cabinRepository.findByActiveTrue();

        Map<String, Boolean> map = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        int length = start.lengthOfMonth();

        for (int d = 1; d <= length; d++) {
            LocalDate day = LocalDate.of(year, month, d);
            String dayStr = day.toString();

            // Una fecha está disponible si al menos una cabaña está disponible ese día
            boolean isAvailable = activeCabins.stream()
                    .anyMatch(cabin -> isDateAvailable(cabin.getId(), day));

            map.put(dayStr, isAvailable);
        }

        return map;
    }

    // Nuevos métodos con lógica real
    @Override
    public List<String> getAvailableDatesForCabin(Long cabinId) {
        // Validar que la cabaña existe y está activa
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new EntityNotFoundException("Cabaña no encontrada"));

        if (!cabin.getActive()) {
            throw new IllegalArgumentException("Cabaña no está activa");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(3); // Próximos 3 meses

        return getAvailableDatesInRange(cabinId, today, endDate)
                .stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Boolean> getAvailabilityCalendarForCabin(Long cabinId, int year, int month) {
        // Validar que la cabaña existe y está activa
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new EntityNotFoundException("Cabaña no encontrada"));

        if (!cabin.getActive()) {
            throw new IllegalArgumentException("Cabaña no está activa");
        }

        Map<String, Boolean> map = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        int length = start.lengthOfMonth();

        for (int d = 1; d <= length; d++) {
            LocalDate day = LocalDate.of(year, month, d);
            map.put(day.toString(), isDateAvailable(cabinId, day));
        }

        return map;
    }

    @Override
    public boolean isCabinAvailable(Long cabinId, LocalDate startDate, LocalDate endDate) {
        // Validar que la cabaña existe y está activa
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new EntityNotFoundException("Cabaña no encontrada"));

        if (!cabin.getActive()) {
            return false;
        }

        // Verificar reservas activas
        List<Reservation> activeReservations = reservationRepository
                .findActiveReservationsByCabinAndDateRange(cabinId, startDate, endDate);

        if (!activeReservations.isEmpty()) {
            return false;
        }

        // Verificar bloques de disponibilidad
        List<AvailabilityBlock> blocks = availabilityBlockRepository
                .findByCabinAndDateRange(cabinId, startDate, endDate);

        return AvailabilityPolicies.respectsMandatoryBlockRanges(
                startDate, endDate, cabinId, blocks);
    }

    @Override
    public List<LocalDate> getAvailableDatesInRange(Long cabinId, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> availableDates = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (isDateAvailable(cabinId, current)) {
                availableDates.add(current);
            }
            current = current.plusDays(1);
        }

        return availableDates;
    }

    /**
     * Verificar si una fecha específica está disponible para una cabaña
     */
    private boolean isDateAvailable(Long cabinId, LocalDate date) {
        // Verificar si está reservada
        if (reservationRepository.isCabinReservedOnDate(cabinId, date)) {
            return false;
        }

        // Verificar si está bloqueada
        if (availabilityBlockRepository.isDateBlocked(cabinId, date)) {
            return false;
        }

        return true;
    }
}

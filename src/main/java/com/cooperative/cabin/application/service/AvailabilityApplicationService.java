package com.cooperative.cabin.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AvailabilityApplicationService {
    // Métodos existentes (mantener compatibilidad)
    List<String> getAvailableDates();

    Map<String, Boolean> getAvailabilityCalendar(int year, int month);

    // Nuevos métodos para consulta por cabaña
    List<String> getAvailableDatesForCabin(Long cabinId);

    Map<String, Boolean> getAvailabilityCalendarForCabin(Long cabinId, int year, int month);

    boolean isCabinAvailable(Long cabinId, LocalDate startDate, LocalDate endDate);

    List<LocalDate> getAvailableDatesInRange(Long cabinId, LocalDate startDate, LocalDate endDate);
}

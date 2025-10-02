package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AvailabilityPolicies {

    public static boolean respectsMandatoryBlockRanges(LocalDate requestStart, LocalDate requestEnd, Long cabinId,
            List<AvailabilityBlock> blocks) {
        // La reserva debe coincidir exactamente con el rango cuando cae dentro de un
        // bloqueo
        for (AvailabilityBlock block : blocks) {
            if (!block.getCabin().getId().equals(cabinId))
                continue;
            LocalDate bStart = block.getStartDate();
            LocalDate bEnd = block.getEndDate();
            boolean overlaps = !(requestEnd.isBefore(bStart) || requestStart.isAfter(bEnd));
            if (overlaps) {
                // Si se solapa, solo se permite si coincide exactamente con el rango bloqueado
                // completo
                boolean exact = requestStart.equals(bStart) && requestEnd.equals(bEnd);
                if (!exact)
                    return false;
            }
        }
        return true;
    }

    // Nuevas políticas de negocio

    /**
     * Verifica si una reserva respeta la política de estadía mínima
     * 
     * @param startDate Fecha de inicio de la reserva
     * @param endDate   Fecha de fin de la reserva
     * @param minDays   Días mínimos de estadía requeridos
     * @return true si respeta la política de estadía mínima
     */
    public static boolean respectsMinimumStayPolicy(LocalDate startDate, LocalDate endDate, int minDays) {
        if (minDays <= 0) {
            return true; // Sin restricción de días mínimos
        }
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return daysBetween >= minDays;
    }

    /**
     * Verifica si una reserva respeta la política de reserva anticipada
     * 
     * @param startDate      Fecha de inicio de la reserva
     * @param maxAdvanceDays Días máximos de anticipación permitidos
     * @return true si respeta la política de reserva anticipada
     */
    public static boolean respectsAdvanceBookingPolicy(LocalDate startDate, int maxAdvanceDays) {
        if (maxAdvanceDays <= 0) {
            return true; // Sin restricción de anticipación
        }
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(maxAdvanceDays);
        return !startDate.isAfter(maxDate);
    }

    /**
     * Verifica si una reserva está dentro de la ventana de reserva permitida
     * 
     * @param startDate Fecha de inicio de la reserva
     * @param endDate   Fecha de fin de la reserva
     * @return true si está dentro de la ventana de reserva
     */
    public static boolean isWithinBookingWindow(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(1); // No se puede reservar para hoy
        LocalDate maxDate = today.plusMonths(12); // Máximo 12 meses adelante

        return !startDate.isBefore(minDate) && !startDate.isAfter(maxDate);
    }

    /**
     * Verifica si una reserva respeta la política de días mínimos entre reservas
     * 
     * @param newStartDate         Fecha de inicio de la nueva reserva
     * @param existingReservations Lista de reservas existentes
     * @param minDaysBetween       Días mínimos requeridos entre reservas
     * @return true si respeta la política de días entre reservas
     */
    public static boolean respectsMinimumDaysBetweenReservations(
            LocalDate newStartDate,
            List<Reservation> existingReservations,
            int minDaysBetween) {

        if (minDaysBetween <= 0) {
            return true; // Sin restricción de días entre reservas
        }

        for (Reservation reservation : existingReservations) {
            LocalDate existingEndDate = reservation.getEndDate();
            long daysBetween = ChronoUnit.DAYS.between(existingEndDate, newStartDate);

            if (daysBetween < minDaysBetween) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si una fecha está en el pasado
     * 
     * @param date Fecha a verificar
     * @return true si la fecha está en el pasado
     */
    public static boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Verifica si el rango de fechas es válido (fecha inicio < fecha fin)
     * 
     * @param startDate Fecha de inicio
     * @param endDate   Fecha de fin
     * @return true si el rango es válido
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        return !startDate.isAfter(endDate);
    }

    /**
     * Verifica si una reserva respeta todas las políticas de disponibilidad
     * 
     * @param startDate            Fecha de inicio
     * @param endDate              Fecha de fin
     * @param minStayDays          Días mínimos de estadía
     * @param maxAdvanceDays       Días máximos de anticipación
     * @param minDaysBetween       Días mínimos entre reservas
     * @param existingReservations Reservas existentes
     * @return true si respeta todas las políticas
     */
    public static boolean respectsAllAvailabilityPolicies(
            LocalDate startDate,
            LocalDate endDate,
            int minStayDays,
            int maxAdvanceDays,
            int minDaysBetween,
            List<Reservation> existingReservations) {

        // Verificar rango de fechas válido
        if (!isValidDateRange(startDate, endDate)) {
            return false;
        }

        // Verificar que no esté en el pasado
        if (isDateInPast(startDate)) {
            return false;
        }

        // Verificar ventana de reserva
        if (!isWithinBookingWindow(startDate, endDate)) {
            return false;
        }

        // Verificar estadía mínima
        if (!respectsMinimumStayPolicy(startDate, endDate, minStayDays)) {
            return false;
        }

        // Verificar reserva anticipada
        if (!respectsAdvanceBookingPolicy(startDate, maxAdvanceDays)) {
            return false;
        }

        // Verificar días entre reservas
        if (!respectsMinimumDaysBetweenReservations(startDate, existingReservations, minDaysBetween)) {
            return false;
        }

        return true;
    }
}

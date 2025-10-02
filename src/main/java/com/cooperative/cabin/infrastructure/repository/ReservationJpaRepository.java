package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Id(Long userId);

    @Query("select max(r.createdAt) from Reservation r where r.user.id = :userId")
    LocalDate findLastCreatedAtDate(Long userId);

    List<Reservation> findByStatusAndStartDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

    List<Reservation> findByStatusAndEndDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

    long countByStatus(ReservationStatus status);

    // Métodos para consulta de disponibilidad por cabaña

    /**
     * Consultar reservas activas por cabaña en un rango de fechas
     * Reservas activas: PENDING, CONFIRMED, IN_USE
     */
    @Query("SELECT r FROM Reservation r WHERE r.cabin.id = :cabinId " +
            "AND r.status IN ('PENDING', 'CONFIRMED', 'IN_USE') " +
            "AND NOT (r.endDate < :startDate OR r.startDate > :endDate)")
    List<Reservation> findActiveReservationsByCabinAndDateRange(
            @Param("cabinId") Long cabinId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Consultar reservas activas por cabaña
     */
    List<Reservation> findByCabin_IdAndStatusIn(Long cabinId, List<ReservationStatus> statuses);

    /**
     * Verificar si una cabaña está reservada en una fecha específica
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.cabin.id = :cabinId " +
            "AND r.status IN ('PENDING', 'CONFIRMED', 'IN_USE') " +
            "AND :date >= r.startDate AND :date <= r.endDate")
    boolean isCabinReservedOnDate(@Param("cabinId") Long cabinId, @Param("date") LocalDate date);

    /**
     * Obtener todas las reservas activas de una cabaña
     */
    @Query("SELECT r FROM Reservation r WHERE r.cabin.id = :cabinId " +
            "AND r.status IN ('PENDING', 'CONFIRMED', 'IN_USE') " +
            "ORDER BY r.startDate ASC")
    List<Reservation> findActiveReservationsByCabin(@Param("cabinId") Long cabinId);
}

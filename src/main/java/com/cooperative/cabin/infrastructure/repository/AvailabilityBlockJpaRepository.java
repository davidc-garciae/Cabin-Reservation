package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityBlockJpaRepository extends JpaRepository<AvailabilityBlock, Long> {
    List<AvailabilityBlock> findByCabin_Id(Long cabinId);

    // Métodos para consulta de disponibilidad por cabaña

    /**
     * Consultar bloques de disponibilidad por cabaña en un rango de fechas
     */
    @Query("SELECT ab FROM AvailabilityBlock ab WHERE ab.cabin.id = :cabinId " +
            "AND NOT (ab.endDate < :startDate OR ab.startDate > :endDate) " +
            "ORDER BY ab.startDate ASC")
    List<AvailabilityBlock> findByCabinAndDateRange(
            @Param("cabinId") Long cabinId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Verificar si una fecha específica está bloqueada para una cabaña
     */
    @Query("SELECT COUNT(ab) > 0 FROM AvailabilityBlock ab WHERE ab.cabin.id = :cabinId " +
            "AND :date >= ab.startDate AND :date <= ab.endDate")
    boolean isDateBlocked(@Param("cabinId") Long cabinId, @Param("date") LocalDate date);

    /**
     * Obtener todos los bloques de disponibilidad de una cabaña ordenados por fecha
     */
    @Query("SELECT ab FROM AvailabilityBlock ab WHERE ab.cabin.id = :cabinId " +
            "ORDER BY ab.startDate ASC")
    List<AvailabilityBlock> findAllByCabinOrderByStartDate(@Param("cabinId") Long cabinId);
}

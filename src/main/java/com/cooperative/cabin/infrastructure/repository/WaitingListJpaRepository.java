package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.WaitingList;
import com.cooperative.cabin.domain.model.WaitingList.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListJpaRepository extends JpaRepository<WaitingList, Long> {

    // Encontrar el próximo candidato a notificar por cabaña y rango (orden por
    // posición y fecha de creación)
    @Query("select w from WaitingList w where w.cabin.id = :cabinId and w.status = 'PENDING' " +
            "and w.requestedStartDate <= :endDate and w.requestedEndDate >= :startDate " +
            "order by w.position asc, w.createdAt asc")
    List<WaitingList> findNextPendingByCabinAndOverlap(@Param("cabinId") Long cabinId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Verificar duplicados activos para un mismo usuario/cabaña/rango
    @Query("select w from WaitingList w where w.user.id = :userId and w.cabin.id = :cabinId " +
            "and w.requestedStartDate = :startDate and w.requestedEndDate = :endDate " +
            "and w.status in ('PENDING','NOTIFIED')")
    List<WaitingList> findActiveDuplicates(@Param("userId") Long userId,
            @Param("cabinId") Long cabinId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Expirar notificaciones vencidas (job)
    @Modifying
    @Query("update WaitingList w set w.status = 'EXPIRED', w.lastStatusChangeAt = :now " +
            "where w.status = 'NOTIFIED' and w.notifyExpiresAt < :now")
    int expireNotifiedOlderThan(@Param("now") LocalDateTime now);

    Optional<WaitingList> findFirstByNotifyTokenAndStatus(String notifyToken, Status status);
}

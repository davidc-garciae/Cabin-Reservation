package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    @Query("select max(r.createdAt) from Reservation r where r.userId = :userId")
    LocalDate findLastCreatedAtDate(Long userId);

    List<Reservation> findByStatusAndStartDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

    List<Reservation> findByStatusAndEndDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

    long countByStatus(ReservationStatus status);
}

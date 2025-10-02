package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.presentation.dto.AdminMetricsResponse;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminMetricsService {

    private final ReservationJpaRepository reservationRepository;
    private final UserJpaRepository userRepository;
    private final CabinJpaRepository cabinRepository;

    public AdminMetricsService(ReservationJpaRepository reservationRepository,
            UserJpaRepository userRepository,
            CabinJpaRepository cabinRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.cabinRepository = cabinRepository;
    }

    public AdminMetricsResponse getSummary() {
        // Obtener métricas reales de la base de datos
        long totalReservations = reservationRepository.count();
        long pendingReservations = reservationRepository.countByStatus(ReservationStatus.PENDING);
        long confirmedReservations = reservationRepository.countByStatus(ReservationStatus.CONFIRMED);
        long inUseReservations = reservationRepository.countByStatus(ReservationStatus.IN_USE);
        long completedReservations = reservationRepository.countByStatus(ReservationStatus.COMPLETED);
        long cancelledReservations = reservationRepository.countByStatus(ReservationStatus.CANCELLED);

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long totalCabins = cabinRepository.count();
        long activeCabins = cabinRepository.findByActiveTrue().size();

        // Calcular tasa de ocupación aproximada
        double occupancyRate = calculateOccupancyRate(confirmedReservations, inUseReservations, activeCabins);

        return new AdminMetricsResponse(
                totalReservations,
                pendingReservations,
                confirmedReservations,
                inUseReservations,
                completedReservations,
                cancelledReservations,
                totalUsers,
                activeUsers,
                totalCabins,
                activeCabins,
                occupancyRate);
    }

    private double calculateOccupancyRate(long confirmed, long inUse, long activeCabins) {
        if (activeCabins <= 0) {
            return 0.0;
        }
        long busyCabins = confirmed + inUse;
        return Math.round((busyCabins * 100.0) / activeCabins * 100.0) / 100.0; // Redondear a 2 decimales
    }
}

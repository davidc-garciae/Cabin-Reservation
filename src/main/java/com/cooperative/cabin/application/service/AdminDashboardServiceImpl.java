package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.presentation.dto.AdminDashboardResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;

@Service
@Profile("!test")
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ReservationJpaRepository reservationRepository;
    private final UserJpaRepository userRepository;
    private final CabinJpaRepository cabinRepository;
    private final MeterRegistry meterRegistry;

    public AdminDashboardServiceImpl(ReservationJpaRepository reservationRepository,
            UserJpaRepository userRepository,
            CabinJpaRepository cabinRepository,
            MeterRegistry meterRegistry) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.cabinRepository = cabinRepository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public AdminDashboardResponse getSummary() {
        long total = reservationRepository.count();
        long pending = reservationRepository.countByStatus(ReservationStatus.PENDING);
        long confirmed = reservationRepository.countByStatus(ReservationStatus.CONFIRMED);
        long inUse = reservationRepository.countByStatus(ReservationStatus.IN_USE);
        long completed = reservationRepository.countByStatus(ReservationStatus.COMPLETED);
        long cancelled = reservationRepository.countByStatus(ReservationStatus.CANCELLED);

        long activeUsers = userRepository.countByActiveTrue();
        long activeCabins = cabinRepository.findByActiveTrue().size();

        int occupancyRate = computeOccupancyApprox(confirmed, inUse, activeCabins);

        // Métricas adicionales (Micrometer), si existen
        Integer reservationsCreated = toIntegerSafe(getCounter("reservations.created"));
        Integer reservationsCancelledMetric = toIntegerSafe(getCounter("reservations.cancelled"));
        Integer statusTransitions = toIntegerSafe(getCounter("reservations.status.transition"));
        Integer schedulerTransitions = toIntegerSafe(getCounter("scheduler.reservations.transition"));

        return new AdminDashboardResponse(
                total, pending, confirmed, inUse, completed, cancelled,
                activeUsers, activeCabins, occupancyRate,
                reservationsCreated, reservationsCancelledMetric, statusTransitions, schedulerTransitions);
    }

    private int computeOccupancyApprox(long confirmed, long inUse, long activeCabins) {
        if (activeCabins <= 0)
            return 0;
        long busy = confirmed + inUse;
        long pct = Math.round((busy * 100.0) / activeCabins);
        return (int) Math.max(0, Math.min(100, pct));
    }

    private Double getCounter(String name) {
        if (meterRegistry == null)
            return null;
        try {
            var c = meterRegistry.find(name).counter();
            return c != null ? c.count() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Integer toIntegerSafe(Double value) {
        if (value == null)
            return 0;
        try {
            return (int) Math.round(value);
        } catch (Exception e) {
            return 0;
        }
    }
}

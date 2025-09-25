package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public class SchedulingApplicationService {

    private final ReservationSchedulerRepository repository;
    private final BusinessMetrics businessMetrics;

    public SchedulingApplicationService(ReservationSchedulerRepository repository) {
        this.repository = repository;
        this.businessMetrics = null;
    }

    public SchedulingApplicationService(ReservationSchedulerRepository repository, BusinessMetrics businessMetrics) {
        this.repository = repository;
        this.businessMetrics = businessMetrics;
    }

    public void runStartDateTransitions(LocalDate today) {
        List<Reservation> toStart = repository.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED,
                today);
        for (Reservation r : toStart) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.IN_USE);
                repository.save(r);
                if (businessMetrics != null)
                    businessMetrics.incrementSchedulerTransition("start");
            }
        }
    }

    public void runEndDateTransitions(LocalDate today) {
        List<Reservation> toComplete = repository.findByStatusAndEndDateLessThanEqual(ReservationStatus.IN_USE, today);
        for (Reservation r : toComplete) {
            if (r.getStatus() == ReservationStatus.IN_USE) {
                r.setStatus(ReservationStatus.COMPLETED);
                repository.save(r);
                if (businessMetrics != null)
                    businessMetrics.incrementSchedulerTransition("end");
            }
        }
    }

    public interface ReservationSchedulerRepository {
        List<Reservation> findByStatusAndStartDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

        List<Reservation> findByStatusAndEndDateLessThanEqual(ReservationStatus status, LocalDate dateInclusive);

        Reservation save(Reservation reservation);
    }
}

package com.cooperative.cabin.infrastructure.scheduler;

import com.cooperative.cabin.application.service.SchedulingApplicationService;
import com.cooperative.cabin.application.service.SchedulingApplicationService.ReservationSchedulerRepository;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
class SchedulingConfig {

    @Bean
    SchedulingApplicationService schedulingApplicationService(ReservationSchedulerRepository adapter,
            io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        return new SchedulingApplicationService(adapter,
                new com.cooperative.cabin.application.service.BusinessMetrics(meterRegistry));
    }

    @Bean
    ReservationSchedulerRepository schedulerRepositoryAdapter(ReservationJpaRepository jpaRepository) {
        return new ReservationSchedulerRepository() {
            @Override
            public List<Reservation> findByStatusAndStartDateLessThanEqual(ReservationStatus status,
                    LocalDate dateInclusive) {
                return jpaRepository.findByStatusAndStartDateLessThanEqual(status, dateInclusive);
            }

            @Override
            public List<Reservation> findByStatusAndEndDateLessThanEqual(ReservationStatus status,
                    LocalDate dateInclusive) {
                return jpaRepository.findByStatusAndEndDateLessThanEqual(status, dateInclusive);
            }

            @Override
            public Reservation save(Reservation reservation) {
                return jpaRepository.save(reservation);
            }
        };
    }
}

@Component
public class ReservationStatusScheduler {

    private final SchedulingApplicationService schedulingApplicationService;

    public ReservationStatusScheduler(SchedulingApplicationService schedulingApplicationService) {
        this.schedulingApplicationService = schedulingApplicationService;
    }

    // Ejecuta a las 00:05 todos los días
    @Scheduled(cron = "0 5 0 * * *")
    public void runDailyStartTransitions() {
        schedulingApplicationService.runStartDateTransitions(LocalDate.now());
    }

    // Ejecuta a las 00:10 todos los días
    @Scheduled(cron = "0 10 0 * * *")
    public void runDailyEndTransitions() {
        schedulingApplicationService.runEndDateTransitions(LocalDate.now());
    }
}

package com.cooperative.cabin.infrastructure.config;

import com.cooperative.cabin.application.service.BusinessMetrics;
import com.cooperative.cabin.application.service.ConfigurationService;
import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.application.service.WaitingListApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("!test")
public class ReservationApplicationServiceConfig {

    @Bean
    public ReservationApplicationService reservationApplicationService(
            ReservationApplicationService.ReservationRepository reservationRepository,
            ReservationApplicationService.AvailabilityBlockRepository availabilityBlockRepository,
            UserJpaRepository userRepository,
            CabinJpaRepository cabinRepository,
            ReservationApplicationService.ConfigurationService reservationConfig,
            MeterRegistry meterRegistry,
            WaitingListApplicationService waitingListApplicationService) {
        return new ReservationApplicationService(
                reservationRepository,
                availabilityBlockRepository,
                userRepository,
                cabinRepository,
                reservationConfig,
                new BusinessMetrics(meterRegistry),
                waitingListApplicationService);
    }

    @Bean
    public ReservationApplicationService.ReservationRepository reservationRepositoryAdapter(
            ReservationJpaRepository jpaRepository) {
        return new ReservationApplicationService.ReservationRepository() {
            @Override
            public List<Reservation> findByUserId(Long userId) {
                return jpaRepository.findByUser_Id(userId);
            }

            @Override
            public LocalDate findLastCreatedAtDate(Long userId) {
                return jpaRepository.findLastCreatedAtDate(userId);
            }

            @Override
            public Reservation save(Reservation reservation) {
                return jpaRepository.save(reservation);
            }

            @Override
            public Reservation findById(Long reservationId) {
                return jpaRepository.findById(reservationId).orElse(null);
            }
        };
    }

    @Bean
    public ReservationApplicationService.AvailabilityBlockRepository availabilityBlockRepositoryAdapter(
            AvailabilityBlockJpaRepository jpaRepository) {
        return new ReservationApplicationService.AvailabilityBlockRepository() {
            @Override
            public List<AvailabilityBlock> findByCabinId(Long cabinId) {
                return jpaRepository.findByCabin_Id(cabinId);
            }
        };
    }

    @Bean
    public ReservationApplicationService.ConfigurationService reservationConfigurationAdapter(
            ConfigurationService configurationService) {
        return new ReservationApplicationService.ConfigurationService() {
            @Override
            public int getStandardTimeoutDays() {
                // key from defaults: reservation.penalty.days (or similar). Use minutes-days
                // mapping per project
                // Fallback simple
                return (int) configurationService.getDurationMinutes("reservation.timeout.minutes").toMinutes()
                        / (60 * 24);
            }

            @Override
            public int getCancellationTimeoutDays() {
                String days = configurationService.getAll().getOrDefault("reservation.penalty.days", "30");
                try {
                    return Integer.parseInt(days);
                } catch (NumberFormatException e) {
                    return 30;
                }
            }

            @Override
            public int getMaxReservationsPerYear() {
                String max = configurationService.getAll().getOrDefault("reservation.max.per.year", "3");
                try {
                    return Integer.parseInt(max);
                } catch (NumberFormatException e) {
                    return 3;
                }
            }
        };
    }
}

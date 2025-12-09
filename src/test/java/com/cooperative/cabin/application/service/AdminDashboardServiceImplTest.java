package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.presentation.dto.AdminDashboardResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminDashboardServiceImplTest {

    private ReservationJpaRepository reservationRepository;
    private UserJpaRepository userRepository;
    private CabinJpaRepository cabinRepository;
    private MeterRegistry meterRegistry;
    private AdminDashboardServiceImpl service;

    @BeforeEach
    void setup() {
        reservationRepository = mock(ReservationJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        cabinRepository = mock(CabinJpaRepository.class);
        meterRegistry = mock(MeterRegistry.class);
        service = new AdminDashboardServiceImpl(reservationRepository, userRepository, cabinRepository, meterRegistry);
    }

    @Test
    void shouldReturnSummaryWithCounters() {
        when(reservationRepository.count()).thenReturn(10L);
        when(reservationRepository.countByStatus(ReservationStatus.PENDING)).thenReturn(2L);
        when(reservationRepository.countByStatus(ReservationStatus.CONFIRMED)).thenReturn(3L);
        when(reservationRepository.countByStatus(ReservationStatus.IN_USE)).thenReturn(2L);
        when(reservationRepository.countByStatus(ReservationStatus.COMPLETED)).thenReturn(2L);
        when(reservationRepository.countByStatus(ReservationStatus.CANCELLED)).thenReturn(1L);
        when(userRepository.countByActiveTrue()).thenReturn(5L);
        when(cabinRepository.findByActiveTrue()).thenReturn(List.of(new com.cooperative.cabin.domain.model.Cabin(),
                new com.cooperative.cabin.domain.model.Cabin()));

        Counter counter = mock(Counter.class);
        when(counter.count()).thenReturn(4.0);
        var search = mock(io.micrometer.core.instrument.search.Search.class);
        when(search.counter()).thenReturn(counter);
        when(meterRegistry.find(anyString())).thenReturn(search);

        AdminDashboardResponse resp = service.getSummary();

        assertThat(resp.reservationsTotal()).isEqualTo(10);
        assertThat(resp.occupancyRatePercent()).isGreaterThan(0);
        assertThat(resp.reservationsCreated()).isEqualTo(4);
    }

    @Test
    void shouldHandleMissingMetricsGracefully() {
        service = new AdminDashboardServiceImpl(reservationRepository, userRepository, cabinRepository, null);

        when(reservationRepository.count()).thenReturn(0L);
        when(reservationRepository.countByStatus(any())).thenReturn(0L);
        when(userRepository.countByActiveTrue()).thenReturn(0L);
        when(cabinRepository.findByActiveTrue()).thenReturn(List.of());

        AdminDashboardResponse resp = service.getSummary();

        assertThat(resp.occupancyRatePercent()).isZero();
        assertThat(resp.reservationsCreated()).isZero();
    }
}


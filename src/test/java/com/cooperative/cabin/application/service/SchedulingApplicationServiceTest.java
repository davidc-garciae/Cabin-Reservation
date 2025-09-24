package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SchedulingApplicationServiceTest {

    @Test
    void runStartDateTransitions_movesConfirmedToInUse_onOrBeforeToday() {
        SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                SchedulingApplicationService.ReservationSchedulerRepository.class);
        SchedulingApplicationService service = new SchedulingApplicationService(repo);

        LocalDate today = LocalDate.of(2025, 1, 10);
        Reservation r1 = new Reservation(1L, 10L, 1L, today.minusDays(1), today.plusDays(1), 2,
                ReservationStatus.CONFIRMED);
        Reservation r2 = new Reservation(2L, 11L, 1L, today, today.plusDays(2), 2, ReservationStatus.CONFIRMED);
        when(repo.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, today))
                .thenReturn(List.of(r1, r2));

        service.runStartDateTransitions(today);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(repo, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .allSatisfy(updated -> assertThat(updated.getStatus()).isEqualTo(ReservationStatus.IN_USE));
    }

    @Test
    void runEndDateTransitions_movesInUseToCompleted_onOrBeforeToday() {
        SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                SchedulingApplicationService.ReservationSchedulerRepository.class);
        SchedulingApplicationService service = new SchedulingApplicationService(repo);

        LocalDate today = LocalDate.of(2025, 1, 15);
        Reservation r1 = new Reservation(3L, 12L, 1L, today.minusDays(5), today.minusDays(1), 2,
                ReservationStatus.IN_USE);
        Reservation r2 = new Reservation(4L, 13L, 1L, today.minusDays(3), today, 2, ReservationStatus.IN_USE);
        when(repo.findByStatusAndEndDateLessThanEqual(ReservationStatus.IN_USE, today))
                .thenReturn(List.of(r1, r2));

        service.runEndDateTransitions(today);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(repo, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .allSatisfy(updated -> assertThat(updated.getStatus()).isEqualTo(ReservationStatus.COMPLETED));
    }

    @Test
    void runStartDateTransitions_isIdempotent_doesNotChangeAlreadyInUse() {
        SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                SchedulingApplicationService.ReservationSchedulerRepository.class);
        SchedulingApplicationService service = new SchedulingApplicationService(repo);

        LocalDate today = LocalDate.of(2025, 1, 10);
        Reservation already = new Reservation(5L, 14L, 1L, today.minusDays(1), today.plusDays(1), 2,
                ReservationStatus.IN_USE);
        when(repo.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, today))
                .thenReturn(List.of());

        // No confirmed reservations returned -> no save
        service.runStartDateTransitions(today);
        verify(repo, never()).save(any());

        // Even if mistakenly returned, service should only update CONFIRMED -> IN_USE
        when(repo.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, today))
                .thenReturn(List.of(already));
        service.runStartDateTransitions(today);
        verify(repo, never()).save(already);
    }

    @Test
    void runEndDateTransitions_isIdempotent_doesNotChangeAlreadyCompleted() {
        SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                SchedulingApplicationService.ReservationSchedulerRepository.class);
        SchedulingApplicationService service = new SchedulingApplicationService(repo);

        LocalDate today = LocalDate.of(2025, 1, 15);
        Reservation completed = new Reservation(6L, 15L, 1L, today.minusDays(3), today.minusDays(1), 2,
                ReservationStatus.COMPLETED);
        when(repo.findByStatusAndEndDateLessThanEqual(ReservationStatus.IN_USE, today))
                .thenReturn(List.of());

        service.runEndDateTransitions(today);
        verify(repo, never()).save(any());

        when(repo.findByStatusAndEndDateLessThanEqual(ReservationStatus.IN_USE, today))
                .thenReturn(List.of(completed));
        service.runEndDateTransitions(today);
        verify(repo, never()).save(completed);
    }
}


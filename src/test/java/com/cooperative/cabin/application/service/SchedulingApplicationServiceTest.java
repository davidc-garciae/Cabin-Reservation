package com.cooperative.cabin.application.service;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.model.Cabin;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
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
                User user1 = TestEntityFactory.createUser(10L, "user1@test.com", "12345678");
                User user2 = TestEntityFactory.createUser(11L, "user2@test.com", "12345679");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                Reservation r1 = TestEntityFactory.createReservation(user1, cabin, today.minusDays(1),
                                today.plusDays(1), 2,
                                ReservationStatus.CONFIRMED);
                r1.setId(1L);
                Reservation r2 = TestEntityFactory.createReservation(user2, cabin, today, today.plusDays(2), 2,
                                ReservationStatus.CONFIRMED);
                r2.setId(2L);
                when(repo.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, today))
                                .thenReturn(List.of(r1, r2));

                service.runStartDateTransitions(today);

                ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
                verify(repo, times(2)).save(captor.capture());
                assertThat(captor.getAllValues())
                                .allSatisfy(updated -> assertThat(updated.getStatus())
                                                .isEqualTo(ReservationStatus.IN_USE));
        }

        @Test
        void runEndDateTransitions_movesInUseToCompleted_onOrBeforeToday() {
                SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                                SchedulingApplicationService.ReservationSchedulerRepository.class);
                SchedulingApplicationService service = new SchedulingApplicationService(repo);

                LocalDate today = LocalDate.of(2025, 1, 15);
                User user3 = TestEntityFactory.createUser(12L, "user3@test.com", "12345680");
                User user4 = TestEntityFactory.createUser(13L, "user4@test.com", "12345681");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                Reservation r1 = TestEntityFactory.createReservation(user3, cabin, today.minusDays(5),
                                today.minusDays(1), 2,
                                ReservationStatus.IN_USE);
                r1.setId(3L);
                Reservation r2 = TestEntityFactory.createReservation(user4, cabin, today.minusDays(3), today, 2,
                                ReservationStatus.IN_USE);
                r2.setId(4L);
                when(repo.findByStatusAndEndDateLessThanEqual(ReservationStatus.IN_USE, today))
                                .thenReturn(List.of(r1, r2));

                service.runEndDateTransitions(today);

                ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
                verify(repo, times(2)).save(captor.capture());
                assertThat(captor.getAllValues())
                                .allSatisfy(updated -> assertThat(updated.getStatus())
                                                .isEqualTo(ReservationStatus.COMPLETED));
        }

        @Test
        void runStartDateTransitions_isIdempotent_doesNotChangeAlreadyInUse() {
                SchedulingApplicationService.ReservationSchedulerRepository repo = mock(
                                SchedulingApplicationService.ReservationSchedulerRepository.class);
                SchedulingApplicationService service = new SchedulingApplicationService(repo);

                LocalDate today = LocalDate.of(2025, 1, 10);
                User user5 = TestEntityFactory.createUser(14L, "user5@test.com", "12345682");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                Reservation already = TestEntityFactory.createReservation(user5, cabin, today.minusDays(1),
                                today.plusDays(1), 2,
                                ReservationStatus.IN_USE);
                already.setId(5L);
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
                User user6 = TestEntityFactory.createUser(15L, "user6@test.com", "12345683");
                Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
                Reservation completed = TestEntityFactory.createReservation(user6, cabin, today.minusDays(3),
                                today.minusDays(1), 2,
                                ReservationStatus.COMPLETED);
                completed.setId(6L);
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

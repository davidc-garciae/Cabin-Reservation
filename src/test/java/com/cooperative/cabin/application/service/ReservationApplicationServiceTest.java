package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReservationApplicationServiceTest {

        private ReservationApplicationService.ReservationRepository reservationRepository;
        private ReservationApplicationService.AvailabilityBlockRepository availabilityBlockRepository;
        private ReservationApplicationService.ConfigurationService configurationService;
        private ReservationApplicationService service;
        private BusinessMetrics businessMetrics;

        @BeforeEach
        void setup() {
                reservationRepository = Mockito.mock(ReservationApplicationService.ReservationRepository.class);
                availabilityBlockRepository = Mockito
                                .mock(ReservationApplicationService.AvailabilityBlockRepository.class);
                configurationService = Mockito.mock(ReservationApplicationService.ConfigurationService.class);
                businessMetrics = Mockito.mock(BusinessMetrics.class);
                service = new ReservationApplicationService(reservationRepository, availabilityBlockRepository,
                                configurationService, businessMetrics);
        }

        @Test
        void createPreReservation_happyPath() {
                Long userId = 1L;
                Long cabinId = 10L;
                when(reservationRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
                when(reservationRepository.findLastCreatedAtDate(userId)).thenReturn(LocalDate.now().minusDays(40));
                when(configurationService.getStandardTimeoutDays()).thenReturn(30);
                when(configurationService.getMaxReservationsPerYear()).thenReturn(3);
                when(availabilityBlockRepository.findByCabinId(cabinId)).thenReturn(Collections.emptyList());
                when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                Reservation r = service.createPreReservation(userId, cabinId, LocalDate.now().plusDays(10),
                                LocalDate.now().plusDays(12), 2);
                assertNotNull(r);
                assertEquals(ReservationStatus.PENDING, r.getStatus());
                assertEquals(userId, r.getUserId());
                Mockito.verify(businessMetrics).incrementReservationCreated();
        }

        @Test
        void createPreReservation_fails_whenUserHasActive() {
                Long userId = 1L;
                Long cabinId = 10L;
                List<Reservation> existing = List.of(new Reservation(1L, userId, cabinId, LocalDate.now(),
                                LocalDate.now().plusDays(1), 2, ReservationStatus.PENDING));
                when(reservationRepository.findByUserId(userId)).thenReturn(existing);

                assertThrows(IllegalStateException.class, () -> service.createPreReservation(userId, cabinId,
                                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), 2));
        }

        @Test
        void cancelReservationByUser_setsCancelled() {
                Long userId = 1L;
                Long cabinId = 10L;
                Long resId = 99L;
                Reservation existing = new Reservation(resId, userId, cabinId, LocalDate.now().plusDays(5),
                                LocalDate.now().plusDays(7), 2, ReservationStatus.PENDING);
                when(reservationRepository.findById(resId)).thenReturn(existing);
                when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                Reservation r = service.cancelByUser(userId, resId);
                assertEquals(ReservationStatus.CANCELLED, r.getStatus());
                Mockito.verify(businessMetrics).incrementReservationCancelled();
        }

        @Test
        void createPreReservation_fails_whenTimeoutNotMet() {
                Long userId = 1L;
                Long cabinId = 10L;
                when(reservationRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
                when(reservationRepository.findLastCreatedAtDate(userId)).thenReturn(LocalDate.now().minusDays(5));
                when(configurationService.getStandardTimeoutDays()).thenReturn(30);
                when(configurationService.getMaxReservationsPerYear()).thenReturn(3);

                assertThrows(IllegalStateException.class, () -> service.createPreReservation(userId, cabinId,
                                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), 2));
        }

        @Test
        void createPreReservation_fails_whenInsideMandatoryBlockWithoutExactMatch() {
                Long userId = 1L;
                Long cabinId = 10L;
                when(reservationRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
                when(reservationRepository.findLastCreatedAtDate(userId)).thenReturn(LocalDate.now().minusDays(40));
                when(configurationService.getStandardTimeoutDays()).thenReturn(30);
                when(availabilityBlockRepository.findByCabinId(cabinId)).thenReturn(List.of(
                                new AvailabilityBlock(1L, cabinId, LocalDate.of(2025, 4, 1),
                                                LocalDate.of(2025, 4, 4))));

                assertThrows(IllegalStateException.class, () -> service.createPreReservation(userId, cabinId,
                                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2), 2));
        }

        @Test
        void changeStatusByAdmin_allowsValidTransitions() {
                Long resId = 200L;
                Long userId = 2L;
                Long cabinId = 20L;
                Reservation pending = new Reservation(resId, userId, cabinId, LocalDate.now().plusDays(3),
                                LocalDate.now().plusDays(5), 2, ReservationStatus.PENDING);
                when(reservationRepository.findById(resId)).thenReturn(pending);
                when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                Reservation confirmed = service.changeStatusByAdmin(resId, ReservationStatus.CONFIRMED);
                assertEquals(ReservationStatus.CONFIRMED, confirmed.getStatus());
                Mockito.verify(businessMetrics).incrementStatusTransition("PENDING", "CONFIRMED");

                when(reservationRepository.findById(resId)).thenReturn(new Reservation(resId, userId, cabinId,
                                pending.getStartDate(), pending.getEndDate(), 2, ReservationStatus.IN_USE));
                Reservation completed = service.changeStatusByAdmin(resId, ReservationStatus.COMPLETED);
                assertEquals(ReservationStatus.COMPLETED, completed.getStatus());

                when(reservationRepository.findById(resId)).thenReturn(new Reservation(resId, userId, cabinId,
                                pending.getStartDate(), pending.getEndDate(), 2, ReservationStatus.CONFIRMED));
                Reservation cancelled = service.changeStatusByAdmin(resId, ReservationStatus.CANCELLED);
                assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        }

        @Test
        void changeStatusByAdmin_rejectsInvalidTransitions() {
                Long resId = 201L;
                Long userId = 3L;
                Long cabinId = 30L;
                Reservation completed = new Reservation(resId, userId, cabinId, LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(2), 2, ReservationStatus.COMPLETED);
                when(reservationRepository.findById(resId)).thenReturn(completed);

                assertThrows(IllegalStateException.class,
                                () -> service.changeStatusByAdmin(resId, ReservationStatus.CONFIRMED));
        }
}

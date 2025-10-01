package com.cooperative.cabin.application.service;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.model.Cabin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReservationApplicationServiceTest {

        private ReservationApplicationService.ReservationRepository reservationRepository;
        private ReservationApplicationService.AvailabilityBlockRepository availabilityBlockRepository;
        private com.cooperative.cabin.infrastructure.repository.UserJpaRepository userRepository;
        private com.cooperative.cabin.infrastructure.repository.CabinJpaRepository cabinRepository;
        private ReservationApplicationService.ConfigurationService configurationService;
        private ReservationApplicationService service;
        private BusinessMetrics businessMetrics;

        @BeforeEach
        void setup() {
                reservationRepository = Mockito.mock(ReservationApplicationService.ReservationRepository.class);
                availabilityBlockRepository = Mockito
                                .mock(ReservationApplicationService.AvailabilityBlockRepository.class);
                userRepository = Mockito.mock(com.cooperative.cabin.infrastructure.repository.UserJpaRepository.class);
                cabinRepository = Mockito
                                .mock(com.cooperative.cabin.infrastructure.repository.CabinJpaRepository.class);
                configurationService = Mockito.mock(ReservationApplicationService.ConfigurationService.class);
                businessMetrics = Mockito.mock(BusinessMetrics.class);
                service = new ReservationApplicationService(reservationRepository, availabilityBlockRepository,
                                userRepository, cabinRepository, configurationService, businessMetrics);
        }

        @Test
        void createPreReservation_happyPath() {
                Long userId = 1L;
                Long cabinId = 10L;

                // Mock User y Cabin usando TestEntityFactory
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);

                when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
                when(cabinRepository.findById(cabinId)).thenReturn(java.util.Optional.of(cabin));
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
                assertEquals(userId, r.getUser().getId());
                Mockito.verify(businessMetrics).incrementReservationCreated();
        }

        @Test
        void createPreReservation_fails_whenUserHasActive() {
                Long userId = 1L;
                Long cabinId = 10L;
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                List<Reservation> existing = List.of(TestEntityFactory.createReservation(user, cabin, LocalDate.now(),
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
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                Reservation existing = TestEntityFactory.createReservation(user, cabin, LocalDate.now().plusDays(5),
                                LocalDate.now().plusDays(7), 2, ReservationStatus.PENDING);
                existing.setId(resId);
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
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                User admin = TestEntityFactory.createAdmin(1L);
                when(availabilityBlockRepository.findByCabinId(cabinId)).thenReturn(List.of(
                                TestEntityFactory.createAvailabilityBlock(1L, cabin, LocalDate.of(2025, 4, 1),
                                                LocalDate.of(2025, 4, 4), "Test block", admin)));

                assertThrows(IllegalStateException.class, () -> service.createPreReservation(userId, cabinId,
                                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2), 2));
        }

        @Test
        void changeStatusByAdmin_allowsValidTransitions() {
                Long resId = 200L;
                Long userId = 2L;
                Long cabinId = 20L;
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                Reservation pending = TestEntityFactory.createReservation(user, cabin, LocalDate.now().plusDays(3),
                                LocalDate.now().plusDays(5), 2, ReservationStatus.PENDING);
                pending.setId(resId);
                when(reservationRepository.findById(resId)).thenReturn(pending);
                when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                Reservation confirmed = service.changeStatusByAdmin(resId, ReservationStatus.CONFIRMED);
                assertEquals(ReservationStatus.CONFIRMED, confirmed.getStatus());
                Mockito.verify(businessMetrics).incrementStatusTransition("PENDING", "CONFIRMED");

                Reservation inUse = TestEntityFactory.createReservation(user, cabin, pending.getStartDate(),
                                pending.getEndDate(), 2, ReservationStatus.IN_USE);
                inUse.setId(resId);
                when(reservationRepository.findById(resId)).thenReturn(inUse);
                Reservation completed = service.changeStatusByAdmin(resId, ReservationStatus.COMPLETED);
                assertEquals(ReservationStatus.COMPLETED, completed.getStatus());

                Reservation confirmed2 = TestEntityFactory.createReservation(user, cabin, pending.getStartDate(),
                                pending.getEndDate(), 2, ReservationStatus.CONFIRMED);
                confirmed2.setId(resId);
                when(reservationRepository.findById(resId)).thenReturn(confirmed2);
                Reservation cancelled = service.changeStatusByAdmin(resId, ReservationStatus.CANCELLED);
                assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        }

        @Test
        void changeStatusByAdmin_rejectsInvalidTransitions() {
                Long resId = 201L;
                Long userId = 3L;
                Long cabinId = 30L;
                User user = TestEntityFactory.createUser(userId, "user@test.com", "12345678");
                Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
                Reservation completed = TestEntityFactory.createReservation(user, cabin, LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(2), 2, ReservationStatus.COMPLETED);
                completed.setId(resId);
                when(reservationRepository.findById(resId)).thenReturn(completed);

                assertThrows(IllegalStateException.class,
                                () -> service.changeStatusByAdmin(resId, ReservationStatus.CONFIRMED));
        }
}

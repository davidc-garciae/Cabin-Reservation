package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.cooperative.cabin.TestAuditingConfiguration;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfiguration.class)
class ReservationJpaRepositoryIT {

        @Resource
        private ReservationJpaRepository reservationRepository;

        @Resource
        private CabinJpaRepository cabinRepository;

        @Resource
        private UserJpaRepository userRepository;

        @Test
        void saveAndFindReservation() {
                // Crear entidades relacionadas
                Cabin cabin = createTestCabin();
                User user = createTestUser();

                Cabin savedCabin = cabinRepository.save(cabin);
                User savedUser = userRepository.save(user);

                // Crear reserva usando método auxiliar
                Reservation reservation = createTestReservation(
                                savedUser,
                                savedCabin,
                                LocalDate.now().plusDays(7),
                                LocalDate.now().plusDays(10),
                                4,
                                ReservationStatus.PENDING,
                                new BigDecimal("150.00"),
                                new BigDecimal("150.00"));

                // Guardar
                Reservation savedReservation = reservationRepository.save(reservation);

                // Verificar
                assertNotNull(savedReservation.getId());
                assertEquals(savedUser.getId(), savedReservation.getUser().getId());
                assertEquals(savedCabin.getId(), savedReservation.getCabin().getId());
                assertEquals(ReservationStatus.PENDING, savedReservation.getStatus());
                assertEquals(4, savedReservation.getNumberOfGuests());
                assertEquals(new BigDecimal("150.00"), savedReservation.getBasePrice());
                assertNotNull(savedReservation.getCreatedAt());
        }

        @Test
        void findByUser_Id() {
                // Crear datos de prueba
                Cabin cabin = createTestCabin();
                User user1 = createTestUser();
                user1.setEmail("user1@test.com");
                user1.setIdentificationNumber("11111111");

                User user2 = createTestUser();
                user2.setEmail("user2@test.com");
                user2.setIdentificationNumber("22222222");

                Cabin savedCabin = cabinRepository.save(cabin);
                User savedUser1 = userRepository.save(user1);
                User savedUser2 = userRepository.save(user2);

                // Crear reservas para diferentes usuarios
                Reservation reservation1 = createTestReservation(savedUser1, savedCabin,
                                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2,
                                ReservationStatus.PENDING, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(reservation1);

                Reservation reservation2 = createTestReservation(savedUser2, savedCabin,
                                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 3,
                                ReservationStatus.CONFIRMED, new BigDecimal("120.00"), new BigDecimal("120.00"));
                reservationRepository.save(reservation2);

                // Buscar reservas por usuario
                List<Reservation> user1Reservations = reservationRepository.findByUser_Id(savedUser1.getId());
                List<Reservation> user2Reservations = reservationRepository.findByUser_Id(savedUser2.getId());

                assertEquals(1, user1Reservations.size());
                assertEquals(1, user2Reservations.size());
                assertEquals(ReservationStatus.PENDING, user1Reservations.get(0).getStatus());
                assertEquals(ReservationStatus.CONFIRMED, user2Reservations.get(0).getStatus());
        }

        @Test
        void findByStatusAndStartDateLessThanEqual() {
                // Crear datos de prueba
                Cabin cabin = createTestCabin();
                User user = createTestUser();
                Cabin savedCabin = cabinRepository.save(cabin);
                User savedUser = userRepository.save(user);

                // Crear reservas con diferentes fechas y estados
                Reservation pastReservation = createTestReservation(savedUser, savedCabin,
                                LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), 2,
                                ReservationStatus.PENDING, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(pastReservation);

                Reservation futureReservation = createTestReservation(savedUser, savedCabin,
                                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 2,
                                ReservationStatus.PENDING, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(futureReservation);

                // Buscar reservas pendientes que ya deberían haber empezado
                List<Reservation> overdueReservations = reservationRepository
                                .findByStatusAndStartDateLessThanEqual(ReservationStatus.PENDING, LocalDate.now());

                assertEquals(1, overdueReservations.size());
                assertEquals(pastReservation.getId(), overdueReservations.get(0).getId());
        }

        @Test
        void countByStatus() {
                // Crear datos de prueba
                Cabin cabin = createTestCabin();
                User user = createTestUser();
                Cabin savedCabin = cabinRepository.save(cabin);
                User savedUser = userRepository.save(user);

                // Crear reservas con diferentes estados
                Reservation pending1 = createTestReservation(savedUser, savedCabin,
                                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2,
                                ReservationStatus.PENDING, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(pending1);

                Reservation pending2 = createTestReservation(savedUser, savedCabin,
                                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 2,
                                ReservationStatus.PENDING, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(pending2);

                Reservation confirmed = createTestReservation(savedUser, savedCabin,
                                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), 2,
                                ReservationStatus.CONFIRMED, new BigDecimal("100.00"), new BigDecimal("100.00"));
                reservationRepository.save(confirmed);

                // Contar por estado
                long pendingCount = reservationRepository.countByStatus(ReservationStatus.PENDING);
                long confirmedCount = reservationRepository.countByStatus(ReservationStatus.CONFIRMED);

                assertEquals(2, pendingCount);
                assertEquals(1, confirmedCount);
        }

        // Métodos auxiliares para crear datos de prueba
        private Cabin createTestCabin() {
                LocalDateTime now = LocalDateTime.now();
                Cabin cabin = new Cabin(
                                "Cabaña de Prueba",
                                "Cabaña para tests de integración",
                                4,
                                2,
                                1,
                                new BigDecimal("100.00"),
                                4,
                                "{\"wifi\": true}",
                                "{\"address\": \"Test Location\"}",
                                LocalTime.of(15, 0), // Check-in 15:00
                                LocalTime.of(11, 0) // Check-out 11:00
                );
                cabin.setCreatedAt(now);
                cabin.setUpdatedAt(now);
                return cabin;
        }

        private User createTestUser() {
                LocalDateTime now = LocalDateTime.now();
                User user = new User();
                user.setEmail("test@cooperativa.com");
                user.setIdentificationNumber("12345678");
                user.setName("Usuario de Prueba");
                user.setPinHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
                user.setRole(User.UserRole.PROFESSOR);
                user.setActive(true);
                user.setCreatedAt(now);
                user.setUpdatedAt(now);
                return user;
        }

        private Reservation createTestReservation(User user, Cabin cabin, LocalDate startDate, LocalDate endDate,
                        int guests, ReservationStatus status, BigDecimal basePrice, BigDecimal finalPrice) {
                LocalDateTime now = LocalDateTime.now();
                Reservation reservation = new Reservation(user, cabin, startDate, endDate, guests, status, basePrice,
                                finalPrice);
                reservation.setCreatedAt(now);
                return reservation;
        }
}

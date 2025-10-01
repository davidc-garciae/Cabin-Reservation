package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.domain.policy.AvailabilityPolicies;
import com.cooperative.cabin.domain.policy.ReservationPolicies;
import com.cooperative.cabin.domain.exception.CabinNotFoundException;
import com.cooperative.cabin.domain.exception.UserNotFoundException;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;

import java.time.LocalDate;
import java.util.List;

public class ReservationApplicationService {

    private final ReservationRepository reservationRepository;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final UserJpaRepository userRepository;
    private final CabinJpaRepository cabinRepository;
    private final ConfigurationService configurationService;
    private final WaitingListApplicationService waitingListService;
    private final BusinessMetrics businessMetrics;

    public ReservationApplicationService(ReservationRepository reservationRepository,
            AvailabilityBlockRepository availabilityBlockRepository,
            UserJpaRepository userRepository,
            CabinJpaRepository cabinRepository,
            ConfigurationService configurationService,
            BusinessMetrics businessMetrics,
            WaitingListApplicationService waitingListService) {
        this.reservationRepository = reservationRepository;
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.userRepository = userRepository;
        this.cabinRepository = cabinRepository;
        this.configurationService = configurationService;
        this.businessMetrics = businessMetrics;
        this.waitingListService = waitingListService;
    }

    // Constructor de compatibilidad para tests existentes
    public ReservationApplicationService(ReservationRepository reservationRepository,
            AvailabilityBlockRepository availabilityBlockRepository,
            UserJpaRepository userRepository,
            CabinJpaRepository cabinRepository,
            ConfigurationService configurationService,
            BusinessMetrics businessMetrics) {
        this(reservationRepository, availabilityBlockRepository, userRepository, cabinRepository,
                configurationService, businessMetrics, null);
    }

    public Reservation createPreReservation(Long userId, Long cabinId, LocalDate start, LocalDate end, int guests) {
        List<Reservation> userReservations = reservationRepository.findByUserId(userId);
        if (ReservationPolicies.hasActiveReservation(userId, userReservations)) {
            throw new IllegalStateException("El usuario ya tiene una reserva activa");
        }

        int maxPerYear = configurationService.getMaxReservationsPerYear();
        int year = start.getYear();
        boolean withinLimit = com.cooperative.cabin.domain.policy.ReservationLimitPolicies
                .withinAnnualLimit(userId, userReservations, maxPerYear, year);
        if (!withinLimit) {
            throw new IllegalStateException("Alcanzó el máximo de reservas permitidas para el año");
        }

        int standardTimeout = configurationService.getStandardTimeoutDays();
        LocalDate lastCreated = reservationRepository.findLastCreatedAtDate(userId);
        if (!ReservationPolicies.isWithinStandardTimeout(lastCreated, standardTimeout, LocalDate.now())) {
            throw new IllegalStateException("Debe esperar antes de crear una nueva reserva");
        }

        List<AvailabilityBlock> blocks = availabilityBlockRepository.findByCabinId(cabinId);
        if (!AvailabilityPolicies.respectsMandatoryBlockRanges(start, end, cabinId, blocks)) {
            throw new IllegalStateException("Debe reservar el rango completo en fechas bloqueadas");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new CabinNotFoundException("Cabin not found with id: " + cabinId));

        // Calcular precios (por ahora usar valores por defecto)
        java.math.BigDecimal basePrice = java.math.BigDecimal.valueOf(100.00);
        java.math.BigDecimal finalPrice = java.math.BigDecimal.valueOf(100.00);

        Reservation r = new Reservation(user, cabin, start, end, guests, ReservationStatus.PENDING, basePrice,
                finalPrice);
        Reservation saved = reservationRepository.save(r);
        if (businessMetrics != null)
            businessMetrics.incrementReservationCreated();
        return saved;
    }

    public Reservation cancelByUser(Long userId, Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId);
        if (r == null || !r.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Reserva no encontrada para el usuario");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        r.setCancelledAt(java.time.LocalDateTime.now());
        Reservation saved = reservationRepository.save(r);
        if (businessMetrics != null)
            businessMetrics.incrementReservationCancelled();

        // Disparar waiting list notify-next automáticamente
        if (waitingListService != null && saved.getCabin() != null) {
            waitingListService.notifyNext(new WaitingListApplicationService.NotifyNextCommand(
                    saved.getCabin().getId(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    4));
        }
        return saved;
    }

    public Reservation changeStatusByAdmin(Long reservationId, ReservationStatus newStatus) {
        Reservation current = reservationRepository.findById(reservationId);
        if (current == null) {
            throw new IllegalStateException("Reserva no encontrada");
        }
        if (!isTransitionAllowed(current.getStatus(), newStatus)) {
            throw new IllegalStateException("Transición de estado no permitida");
        }
        ReservationStatus oldStatus = current.getStatus();
        current.setStatus(newStatus);
        if (newStatus == ReservationStatus.CONFIRMED) {
            current.setConfirmedAt(java.time.LocalDateTime.now());
        }
        Reservation saved = reservationRepository.save(current);
        if (businessMetrics != null)
            businessMetrics.incrementStatusTransition(oldStatus.name(), newStatus.name());

        // Si pasa a CANCELLED, notificar waiting list automáticamente
        if (newStatus == ReservationStatus.CANCELLED && waitingListService != null && saved.getCabin() != null) {
            waitingListService.notifyNext(new WaitingListApplicationService.NotifyNextCommand(
                    saved.getCabin().getId(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    4));
        }
        return saved;
    }

    public List<Reservation> listByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation getByIdForUser(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation == null || !reservation.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Reserva no encontrada para el usuario");
        }
        return reservation;
    }

    public List<Reservation> listAllForAdmin() {
        // mínimo: reutilizamos findByUserId si fuera necesario; en real, habría un
        // findAll
        // Aquí devolvemos vacío por defecto para estructura
        return reservationRepository.findByUserId(null);
    }

    public void deleteByAdmin(Long reservationId) {
        // En una implementación real, repository.deleteById(reservationId)
        // Aquí no hay repo delete, así que no hacemos nada; solo estructura para TDD
        // MVC
    }

    private boolean isTransitionAllowed(ReservationStatus from, ReservationStatus to) {
        switch (from) {
            case PENDING:
                return to == ReservationStatus.CONFIRMED || to == ReservationStatus.CANCELLED;
            case CONFIRMED:
                return to == ReservationStatus.CANCELLED; // IN_USE es automático; admin puede cancelar
            case IN_USE:
                return to == ReservationStatus.COMPLETED || to == ReservationStatus.CANCELLED;
            default:
                return false;
        }
    }

    public interface ReservationRepository {
        List<Reservation> findByUserId(Long userId);

        LocalDate findLastCreatedAtDate(Long userId);

        Reservation save(Reservation reservation);

        Reservation findById(Long reservationId);
    }

    public interface AvailabilityBlockRepository {
        List<AvailabilityBlock> findByCabinId(Long cabinId);
    }

    public interface ConfigurationService {
        int getStandardTimeoutDays();

        int getCancellationTimeoutDays();

        int getMaxReservationsPerYear();
    }
}

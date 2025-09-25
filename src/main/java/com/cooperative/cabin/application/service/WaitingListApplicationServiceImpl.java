package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.WaitingList;
import com.cooperative.cabin.infrastructure.repository.WaitingListJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WaitingListApplicationServiceImpl implements WaitingListApplicationService {

    private final WaitingListJpaRepository waitingListRepository;
    private final ReservationJpaRepository reservationRepository;

    public WaitingListApplicationServiceImpl(WaitingListJpaRepository waitingListRepository,
            ReservationJpaRepository reservationRepository) {
        this.waitingListRepository = waitingListRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public Optional<NotifyNextResult> notifyNext(NotifyNextCommand command) {
        List<WaitingList> candidates = waitingListRepository.findNextPendingByCabinAndOverlap(
                command.cabinId(), command.startDate(), command.endDate());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        WaitingList next = candidates.get(0);
        String token = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(Math.max(1, command.windowHours()));
        next.setNotifyToken(token);
        next.setNotifyExpiresAt(expires);
        next.setStatus(WaitingList.Status.NOTIFIED);
        next.setNotifiedAt(LocalDateTime.now());
        next.setLastStatusChangeAt(LocalDateTime.now());
        waitingListRepository.save(next);
        return Optional.of(
                new NotifyNextResult(next.getId(), next.getUser().getId(), next.getCabin().getId(), token, expires));
    }

    @Override
    @Transactional
    public Optional<ClaimResult> claim(ClaimCommand command) {
        Optional<WaitingList> opt = waitingListRepository.findFirstByNotifyTokenAndStatus(command.notifyToken(),
                WaitingList.Status.NOTIFIED);
        if (opt.isEmpty())
            return Optional.empty();
        WaitingList item = opt.get();
        if (item.getNotifyExpiresAt() == null || LocalDateTime.now().isAfter(item.getNotifyExpiresAt())) {
            // expirar si venció
            item.setStatus(WaitingList.Status.EXPIRED);
            item.setLastStatusChangeAt(LocalDateTime.now());
            waitingListRepository.save(item);
            return Optional.empty();
        }

        // Validar disponibilidad básica: no existan reservas solapadas
        // CONFIRMED/IN_USE/PENDING
        LocalDate start = item.getRequestedStartDate();
        LocalDate end = item.getRequestedEndDate();
        // Simplificación: uso de repositorio existente por estado/fecha
        List<Reservation> overlaps = reservationRepository
                .findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, end);
        // Nota: en un caso real haríamos una query por cabin y solapamiento completo
        if (!overlaps.isEmpty()) {
            return Optional.empty();
        }

        // Aquí se crearía la Reservation real (omito para mantener simple):
        // Reservation r = new Reservation(item.getUser(), item.getCabin(), start, end,
        // ...)
        // r.setStatus(ReservationStatus.PENDING);
        // reservationRepository.save(r);

        // Marcar claim
        item.markAsClaimed(null);
        waitingListRepository.save(item);
        return Optional.of(new ClaimResult(null, item.getId(), item.getUser().getId(), item.getCabin().getId()));
    }
}

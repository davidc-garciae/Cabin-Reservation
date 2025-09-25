package com.cooperative.cabin.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WaitingListApplicationService {

    record NotifyNextCommand(Long cabinId, LocalDate startDate, LocalDate endDate, int windowHours) {
    }

    record NotifyNextResult(Long waitingListId, Long userId, Long cabinId, String notifyToken,
            LocalDateTime notifyExpiresAt) {
    }

    Optional<NotifyNextResult> notifyNext(NotifyNextCommand command);

    record ClaimCommand(String notifyToken, Integer numberOfGuests) {
    }

    record ClaimResult(Long reservationId, Long waitingListId, Long userId, Long cabinId) {
    }

    Optional<ClaimResult> claim(ClaimCommand command);
}

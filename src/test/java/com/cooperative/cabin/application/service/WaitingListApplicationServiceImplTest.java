package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.WaitingList;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import com.cooperative.cabin.infrastructure.repository.WaitingListJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WaitingListApplicationServiceImplTest {

    private WaitingListJpaRepository waitingListRepository;
    private ReservationJpaRepository reservationRepository;
    private WaitingListApplicationServiceImpl service;

    @BeforeEach
    void setup() {
        waitingListRepository = mock(WaitingListJpaRepository.class);
        reservationRepository = mock(ReservationJpaRepository.class);
        service = new WaitingListApplicationServiceImpl(waitingListRepository, reservationRepository);
    }

    @Test
    void shouldNotifyNextCandidate() {
        WaitingList item = new WaitingList();
        item.setId(1L);
        item.setUser(new com.cooperative.cabin.domain.model.User());
        item.getUser().setId(10L);
        item.setCabin(new com.cooperative.cabin.domain.model.Cabin());
        item.getCabin().setId(20L);
        when(waitingListRepository.findNextPendingByCabinAndOverlap(any(), any(), any()))
                .thenReturn(List.of(item));
        when(waitingListRepository.save(any(WaitingList.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.notifyNext(new WaitingListApplicationService.NotifyNextCommand(
                20L, LocalDate.now(), LocalDate.now().plusDays(1), 2));

        assertThat(result).isPresent();
        assertThat(item.getNotifyToken()).isNotBlank();
        assertThat(item.getStatus()).isEqualTo(WaitingList.Status.NOTIFIED);
    }

    @Test
    void shouldReturnEmptyWhenNoCandidates() {
        when(waitingListRepository.findNextPendingByCabinAndOverlap(any(), any(), any()))
                .thenReturn(List.of());

        var result = service.notifyNext(new WaitingListApplicationService.NotifyNextCommand(
                20L, LocalDate.now(), LocalDate.now().plusDays(1), 2));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldExpireClaimWhenTokenExpired() {
        WaitingList item = new WaitingList();
        item.setStatus(WaitingList.Status.NOTIFIED);
        item.setNotifyExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(waitingListRepository.findFirstByNotifyTokenAndStatus("t", WaitingList.Status.NOTIFIED))
                .thenReturn(Optional.of(item));

        var result = service.claim(new WaitingListApplicationService.ClaimCommand("t", 2));

        assertThat(result).isEmpty();
        assertThat(item.getStatus()).isEqualTo(WaitingList.Status.EXPIRED);
        verify(waitingListRepository).save(item);
    }

    @Test
    void shouldClaimWhenAvailable() {
        WaitingList item = new WaitingList();
        item.setStatus(WaitingList.Status.NOTIFIED);
        item.setNotifyExpiresAt(LocalDateTime.now().plusMinutes(5));
        item.setUser(new com.cooperative.cabin.domain.model.User());
        item.getUser().setId(10L);
        item.setCabin(new com.cooperative.cabin.domain.model.Cabin());
        item.getCabin().setId(20L);
        item.setRequestedStartDate(LocalDate.now());
        item.setRequestedEndDate(LocalDate.now().plusDays(1));
        when(waitingListRepository.findFirstByNotifyTokenAndStatus("t", WaitingList.Status.NOTIFIED))
                .thenReturn(Optional.of(item));
        when(reservationRepository.findByStatusAndStartDateLessThanEqual(ReservationStatus.CONFIRMED, item.getRequestedEndDate()))
                .thenReturn(List.of());
        when(waitingListRepository.save(any(WaitingList.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.claim(new WaitingListApplicationService.ClaimCommand("t", 2));

        assertThat(result).isPresent();
        assertThat(item.getStatus()).isEqualTo(WaitingList.Status.CLAIMED);
    }
}


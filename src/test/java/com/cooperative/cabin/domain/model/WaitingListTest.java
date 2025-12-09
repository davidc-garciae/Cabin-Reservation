package com.cooperative.cabin.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WaitingListTest {

    @Test
    void shouldMarkAsNotifiedAndClaimedAndExpired() {
        User user = new User();
        Cabin cabin = new Cabin();
        WaitingList waiting = new WaitingList(user, cabin,
                LocalDate.now(), LocalDate.now().plusDays(1), 2, 1);

        assertEquals(WaitingList.Status.PENDING, waiting.getStatus());
        assertFalse(waiting.isNotified());

        waiting.markAsNotified();
        assertEquals(WaitingList.Status.NOTIFIED, waiting.getStatus());
        assertTrue(waiting.isNotified());
        LocalDateTime notifiedAt = waiting.getNotifiedAt();

        waiting.markAsClaimed(5L);
        assertEquals(WaitingList.Status.CLAIMED, waiting.getStatus());
        assertEquals(5L, waiting.getClaimedReservationId());
        assertNotNull(waiting.getClaimedAt());
        assertTrue(waiting.getClaimedAt().isAfter(notifiedAt) || waiting.getClaimedAt().isEqual(notifiedAt));

        waiting.markAsExpired();
        assertEquals(WaitingList.Status.EXPIRED, waiting.getStatus());
        assertNotNull(waiting.getLastStatusChangeAt());
    }

    @Test
    void shouldExposeIdsSafely() {
        User user = new User();
        user.setId(1L);
        Cabin cabin = new Cabin();
        cabin.setId(2L);

        WaitingList waiting = new WaitingList(user, cabin,
                LocalDate.now(), LocalDate.now().plusDays(1), 2, 1);

        assertEquals(1L, waiting.getUserId());
        assertEquals(2L, waiting.getCabinId());
    }
}


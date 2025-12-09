package com.cooperative.cabin.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    void shouldDetectExpiredToken() {
        User user = new User();
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        PasswordResetToken token = new PasswordResetToken("t", user, past);

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }

    @Test
    void shouldBeValidWhenNotUsedAndNotExpired() {
        User user = new User();
        LocalDateTime future = LocalDateTime.now().plusMinutes(5);
        PasswordResetToken token = new PasswordResetToken("t", user, future);

        assertFalse(token.isExpired());
        assertTrue(token.isValid());
    }
}


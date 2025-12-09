package com.cooperative.cabin.domain.policy;

import com.cooperative.cabin.domain.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordValidatorTest {

    @Test
    void adminShouldRejectShortOrPin() {
        assertThatThrownBy(() -> PasswordValidator.validatePasswordForRole("12345", User.UserRole.ADMIN))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PasswordValidator.validatePasswordForRole("1234", User.UserRole.ADMIN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void adminShouldAcceptLongPassword() {
        PasswordValidator.validatePasswordForRole("strongPass1", User.UserRole.ADMIN);
    }

    @Test
    void professorCanUsePinOrPassword() {
        PasswordValidator.validatePasswordForRole("1234", User.UserRole.PROFESSOR);
        PasswordValidator.validatePasswordForRole("password", User.UserRole.PROFESSOR);
    }

    @Test
    void normalUserShouldRejectInvalid() {
        assertThatThrownBy(() -> PasswordValidator.validatePasswordForRole("123", User.UserRole.RETIREE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void detectPin() {
        assertThat(PasswordValidator.isPin4Digits("1234")).isTrue();
        assertThat(PasswordValidator.isPin4Digits("abcd")).isFalse();
    }
}


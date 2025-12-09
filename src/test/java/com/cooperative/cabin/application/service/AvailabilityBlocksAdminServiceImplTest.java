package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AvailabilityBlocksAdminServiceImplTest {

    private AvailabilityBlockJpaRepository repository;
    private CabinJpaRepository cabinRepository;
    private UserJpaRepository userRepository;
    private AvailabilityBlocksAdminServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(AvailabilityBlockJpaRepository.class);
        cabinRepository = mock(CabinJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        service = new AvailabilityBlocksAdminServiceImpl(repository, cabinRepository, userRepository);
    }

    @Test
    void shouldListBlocks() {
        when(repository.findAll()).thenReturn(List.of(new AvailabilityBlock()));

        assertThat(service.list()).hasSize(1);
    }

    @Test
    void shouldCreateBlockWhenDataIsValid() {
        Cabin cabin = new Cabin();
        cabin.setId(1L);
        User user = new User();
        user.setId(2L);
        when(cabinRepository.findById(1L)).thenReturn(Optional.of(cabin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(repository.save(any(AvailabilityBlock.class))).thenAnswer(inv -> inv.getArgument(0));

        AvailabilityBlock block = service.create(1L, LocalDate.now(), LocalDate.now().plusDays(1), 2L);

        assertThat(block.getCabin()).isEqualTo(cabin);
        assertThat(block.getCreatedBy()).isEqualTo(user);
    }

    @Test
    void shouldValidateDatesOnCreate() {
        when(cabinRepository.findById(1L)).thenReturn(Optional.of(new Cabin()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> service.create(1L, LocalDate.now().plusDays(2), LocalDate.now(), 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailIfCabinOrUserMissing() {
        when(cabinRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> service.create(1L, LocalDate.now(), LocalDate.now(), 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}


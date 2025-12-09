package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.exception.CabinNotFoundException;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CabinApplicationServiceImplTest {

    private CabinJpaRepository cabinRepository;
    private CabinApplicationServiceImpl service;

    @BeforeEach
    void setup() {
        cabinRepository = mock(CabinJpaRepository.class);
        service = new CabinApplicationServiceImpl(cabinRepository);
    }

    @Test
    void shouldGetActiveCabinById() {
        Cabin cabin = new Cabin();
        cabin.setId(1L);
        cabin.setActive(true);
        when(cabinRepository.findById(1L)).thenReturn(Optional.of(cabin));

        CabinResponse resp = service.getCabinById(1L);

        assertThat(resp.id()).isEqualTo(1L);
    }

    @Test
    void shouldFailWhenCabinInactive() {
        Cabin cabin = new Cabin();
        cabin.setId(1L);
        cabin.setActive(false);
        when(cabinRepository.findById(1L)).thenReturn(Optional.of(cabin));

        assertThatThrownBy(() -> service.getCabinById(1L)).isInstanceOf(CabinNotFoundException.class);
    }

    @Test
    void shouldCreateCabin() {
        when(cabinRepository.save(any(Cabin.class))).thenAnswer(inv -> {
            Cabin c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });
        CreateCabinRequest req = new CreateCabinRequest("Cab", "Desc", 2, 1, 1,
                BigDecimal.TEN, 2, null, null, "15:00", "11:00");

        CabinResponse resp = service.createCabin(req);

        assertThat(resp.id()).isEqualTo(2L);
    }

    @Test
    void shouldUpdateCabin() {
        Cabin cabin = new Cabin();
        cabin.setId(1L);
        cabin.setActive(true);
        when(cabinRepository.findById(1L)).thenReturn(Optional.of(cabin));
        when(cabinRepository.save(any(Cabin.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCabinRequest req = new UpdateCabinRequest("Cab2", "Desc2", 4, 2, 1,
                BigDecimal.ONE, 4, true, null, null, "15:00", "11:00");
        CabinResponse resp = service.updateCabin(1L, req);

        assertThat(resp.name()).isEqualTo("Cab2");
    }

    @Test
    void shouldSearchByName() {
        when(cabinRepository.findActiveCabinsByNameContaining("Cab")).thenReturn(List.of(new Cabin()));

        var list = service.searchCabins(null, null, null, "Cab");

        assertThat(list).hasSize(1);
    }
}


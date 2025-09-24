package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservationJpaRepositoryIT {

    @Resource
    private ReservationJpaRepository repository;

    @Test
    void saveAndQueryByUserId() {
        Reservation r = new Reservation(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1), 2,
                ReservationStatus.PENDING);
        repository.save(r);
        List<Reservation> list = repository.findByUserId(1L);
        assertFalse(list.isEmpty());
    }
}

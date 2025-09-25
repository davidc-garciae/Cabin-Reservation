package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.PriceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceRangeJpaRepository extends JpaRepository<PriceRange, Long> {
    List<PriceRange> findByCabin_Id(Long cabinId);

    List<PriceRange> findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long cabinId, LocalDate start,
            LocalDate end);
}

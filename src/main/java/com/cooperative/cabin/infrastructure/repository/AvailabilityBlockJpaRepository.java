package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityBlockJpaRepository extends JpaRepository<AvailabilityBlock, Long> {
    List<AvailabilityBlock> findByCabin_Id(Long cabinId);
}

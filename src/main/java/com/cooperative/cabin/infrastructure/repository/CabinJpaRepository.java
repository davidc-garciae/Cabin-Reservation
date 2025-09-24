package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabinJpaRepository extends JpaRepository<Cabin, Long> {

    /**
     * Find all active cabins
     */
    List<Cabin> findByActiveTrue();

    /**
     * Find cabins by capacity (minimum capacity)
     */
    @Query("SELECT c FROM Cabin c WHERE c.active = true AND c.capacity >= :minCapacity ORDER BY c.capacity ASC")
    List<Cabin> findActiveCabinsByMinCapacity(@Param("minCapacity") Integer minCapacity);

    /**
     * Find cabins by price range
     */
    @Query("SELECT c FROM Cabin c WHERE c.active = true AND c.basePrice BETWEEN :minPrice AND :maxPrice ORDER BY c.basePrice ASC")
    List<Cabin> findActiveCabinsByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Find cabins by name containing (case insensitive)
     */
    @Query("SELECT c FROM Cabin c WHERE c.active = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.name ASC")
    List<Cabin> findActiveCabinsByNameContaining(@Param("name") String name);
}

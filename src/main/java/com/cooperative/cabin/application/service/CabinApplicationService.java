package com.cooperative.cabin.application.service;

import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;

import java.util.List;

public interface CabinApplicationService {

    /**
     * Get all active cabins for users
     */
    List<CabinResponse> getAllActiveCabins();

    /**
     * Get cabin by ID (for users)
     */
    CabinResponse getCabinById(Long id);

    /**
     * Get all cabins for admin (including inactive)
     */
    List<CabinResponse> getAllCabinsForAdmin();

    /**
     * Get cabin by ID for admin
     */
    CabinResponse getCabinByIdForAdmin(Long id);

    /**
     * Create new cabin (admin only)
     */
    CabinResponse createCabin(CreateCabinRequest request);

    /**
     * Update cabin (admin only)
     */
    CabinResponse updateCabin(Long id, UpdateCabinRequest request);

    /**
     * Delete cabin (admin only)
     */
    void deleteCabin(Long id);

    /**
     * Search cabins by criteria
     */
    List<CabinResponse> searchCabins(Integer minCapacity, java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice, String name);
}

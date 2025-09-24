package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.exception.CabinNotFoundException;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;
import com.cooperative.cabin.presentation.mapper.CabinMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CabinApplicationServiceImpl implements CabinApplicationService {

    private static final String CABIN_NOT_FOUND_MESSAGE = "Cabin not found with id: ";

    private final CabinJpaRepository cabinRepository;

    public CabinApplicationServiceImpl(CabinJpaRepository cabinRepository) {
        this.cabinRepository = cabinRepository;
    }

    @Override
    public List<CabinResponse> getAllActiveCabins() {
        return cabinRepository.findByActiveTrue()
                .stream()
                .map(CabinMapper.INSTANCE::toResponse)
                .toList();
    }

    @Override
    public CabinResponse getCabinById(Long id) {
        Cabin cabin = cabinRepository.findById(id)
                .orElseThrow(() -> new CabinNotFoundException(CABIN_NOT_FOUND_MESSAGE + id));

        if (Boolean.FALSE.equals(cabin.getActive())) {
            throw new CabinNotFoundException(CABIN_NOT_FOUND_MESSAGE + id);
        }

        return CabinMapper.INSTANCE.toResponse(cabin);
    }

    @Override
    public List<CabinResponse> getAllCabinsForAdmin() {
        return cabinRepository.findAll()
                .stream()
                .map(CabinMapper.INSTANCE::toResponse)
                .toList();
    }

    @Override
    public CabinResponse getCabinByIdForAdmin(Long id) {
        Cabin cabin = cabinRepository.findById(id)
                .orElseThrow(() -> new CabinNotFoundException(CABIN_NOT_FOUND_MESSAGE + id));
        return CabinMapper.INSTANCE.toResponse(cabin);
    }

    @Override
    @Transactional
    public CabinResponse createCabin(CreateCabinRequest request) {
        Cabin cabin = CabinMapper.INSTANCE.fromCreateRequest(request);

        Cabin savedCabin = cabinRepository.save(cabin);
        return CabinMapper.INSTANCE.toResponse(savedCabin);
    }

    @Override
    @Transactional
    public CabinResponse updateCabin(Long id, UpdateCabinRequest request) {
        Cabin cabin = cabinRepository.findById(id)
                .orElseThrow(() -> new CabinNotFoundException(CABIN_NOT_FOUND_MESSAGE + id));

        CabinMapper.INSTANCE.updateEntity(cabin, request);

        Cabin savedCabin = cabinRepository.save(cabin);
        return CabinMapper.INSTANCE.toResponse(savedCabin);
    }

    @Override
    @Transactional
    public void deleteCabin(Long id) {
        if (!cabinRepository.existsById(id)) {
            throw new CabinNotFoundException(CABIN_NOT_FOUND_MESSAGE + id);
        }
        cabinRepository.deleteById(id);
    }

    @Override
    public List<CabinResponse> searchCabins(Integer minCapacity, java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice, String name) {
        List<Cabin> cabins;

        if (name != null && !name.trim().isEmpty()) {
            cabins = cabinRepository.findActiveCabinsByNameContaining(name.trim());
        } else if (minCapacity != null) {
            cabins = cabinRepository.findActiveCabinsByMinCapacity(minCapacity);
        } else if (minPrice != null && maxPrice != null) {
            cabins = cabinRepository.findActiveCabinsByPriceRange(minPrice, maxPrice);
        } else {
            cabins = cabinRepository.findByActiveTrue();
        }

        return cabins.stream().map(CabinMapper.INSTANCE::toResponse).toList();
    }
}

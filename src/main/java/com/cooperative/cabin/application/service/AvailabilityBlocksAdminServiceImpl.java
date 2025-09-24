package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Profile("!test")
public class AvailabilityBlocksAdminServiceImpl implements AvailabilityBlocksAdminService {

    private final AvailabilityBlockJpaRepository repository;

    public AvailabilityBlocksAdminServiceImpl(AvailabilityBlockJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AvailabilityBlock> list() {
        return repository.findAll();
    }

    @Override
    public AvailabilityBlock create(Long cabinId, LocalDate startDate, LocalDate endDate) {
        AvailabilityBlock block = new AvailabilityBlock(null, cabinId, startDate, endDate);
        return repository.save(block);
    }

    @Override
    public AvailabilityBlock update(Long id, Long cabinId, LocalDate startDate, LocalDate endDate) {
        // Reemplazo simple por id (merge) dado que la entidad no tiene setters
        AvailabilityBlock updated = new AvailabilityBlock(id, cabinId, startDate, endDate);
        return repository.save(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

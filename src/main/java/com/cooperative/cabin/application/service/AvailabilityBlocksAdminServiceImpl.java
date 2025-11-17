package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Profile("!test")
public class AvailabilityBlocksAdminServiceImpl implements AvailabilityBlocksAdminService {

    private final AvailabilityBlockJpaRepository repository;
    private final CabinJpaRepository cabinRepository;
    private final UserJpaRepository userRepository;

    public AvailabilityBlocksAdminServiceImpl(
            AvailabilityBlockJpaRepository repository,
            CabinJpaRepository cabinRepository,
            UserJpaRepository userRepository) {
        this.repository = repository;
        this.cabinRepository = cabinRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AvailabilityBlock> list() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public AvailabilityBlock create(Long cabinId, LocalDate startDate, LocalDate endDate, Long userId) {
        // Cargar entidades necesarias
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new IllegalArgumentException("Cabin not found with id: " + cabinId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Validar fechas
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Crear el bloqueo
        AvailabilityBlock block = new AvailabilityBlock(cabin, startDate, endDate, "Admin block", user);
        return repository.save(block);
    }

    @Override
    @Transactional
    public AvailabilityBlock update(Long id, Long cabinId, LocalDate startDate, LocalDate endDate, Long userId) {
        // Buscar el bloqueo existente
        AvailabilityBlock existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Availability block not found with id: " + id));

        // Cargar entidades necesarias
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow(() -> new IllegalArgumentException("Cabin not found with id: " + cabinId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Validar fechas
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Actualizar campos (usando setters de Lombok @Data)
        existing.setCabin(cabin);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        existing.setReason("Updated by admin");

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

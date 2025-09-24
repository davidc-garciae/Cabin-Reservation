package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.domain.model.AuditLog;
import com.cooperative.cabin.infrastructure.repository.AuditLogJpaRepository;
import com.cooperative.cabin.presentation.dto.AuditLogResponse;
import com.cooperative.cabin.presentation.dto.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAuditLogsService {

    private final AuditLogJpaRepository repository;

    public AdminAuditLogsService(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    public PageResponse<AuditLogResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var slice = repository.findAll(pageable);
        List<AuditLogResponse> items = slice.getContent().stream().map(this::toResponse).toList();
        long total = slice.getTotalElements();
        return new PageResponse<>(items, page, size, total);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(log.getId());
        // userId no existe en la entidad actual; lo omitimos por ahora
        dto.setAction(log.getAction());
        dto.setEntityType(log.getEntityType());
        dto.setEntityId(log.getEntityId() != null ? String.valueOf(log.getEntityId()) : null);
        dto.setOldValues(log.getOldValues());
        dto.setNewValues(log.getNewValues());
        // ipAddress no existe en la entidad actual; lo omitimos por ahora
        dto.setCreatedAt(
                log.getCreatedAt() != null ? log.getCreatedAt().atZone(java.time.ZoneOffset.UTC).toInstant() : null);
        return dto;
    }
}

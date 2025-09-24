package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogJpaRepository extends JpaRepository<AuditLog, Long> {
}


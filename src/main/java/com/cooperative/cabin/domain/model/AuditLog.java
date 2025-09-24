package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "old_values")
    private String oldValues;

    @Column(name = "new_values")
    private String newValues;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(Long id, String action, String entityType, Long entityId, String oldValues, String newValues) {
        this.id = id;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getOldValues() {
        return oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


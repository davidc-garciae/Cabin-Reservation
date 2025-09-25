package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DocumentNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_number", nullable = false, unique = true)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum DocumentStatus {
        ACTIVE, DISABLED
    }

    // Constructors
    public DocumentNumber(String documentNumber, DocumentStatus status) {
        this.documentNumber = documentNumber;
        this.status = status;
    }

    // Business methods
    public boolean isActive() {
        return DocumentStatus.ACTIVE.equals(this.status);
    }

    public boolean isDisabled() {
        return DocumentStatus.DISABLED.equals(this.status);
    }

    public void activate() {
        this.status = DocumentStatus.ACTIVE;
    }

    public void disable() {
        this.status = DocumentStatus.DISABLED;
    }
}

package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list", indexes = {
        @Index(name = "idx_waiting_cabin_dates", columnList = "cabin_id, requested_start_date, requested_end_date"),
        @Index(name = "idx_waiting_status", columnList = "status, cabin_id"),
        @Index(name = "idx_waiting_created_at", columnList = "created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_waiting_unique_request", columnNames = { "user_id", "cabin_id",
                "requested_start_date", "requested_end_date" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WaitingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabin_id", nullable = false)
    private Cabin cabin;

    @Column(name = "requested_start_date", nullable = false)
    private LocalDate requestedStartDate;

    @Column(name = "requested_end_date", nullable = false)
    private LocalDate requestedEndDate;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Column(name = "position", nullable = false)
    private int position;

    // Estado del item de lista de espera
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    // Token de notificación y ventana de oportunidad para reclamar prioridad
    @Column(name = "notify_token", length = 64)
    private String notifyToken;

    @Column(name = "notify_expires_at")
    private LocalDateTime notifyExpiresAt;

    // Trazabilidad de la reclamación (claim)
    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "claimed_reservation_id")
    private Long claimedReservationId;

    // Último cambio de estado (opcional para auditoría liviana)
    @Column(name = "last_status_change_at")
    private LocalDateTime lastStatusChangeAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    // Constructors
    public WaitingList(User user, Cabin cabin, LocalDate requestedStartDate, LocalDate requestedEndDate,
            int numberOfGuests, int position) {
        this.user = user;
        this.cabin = cabin;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
        this.numberOfGuests = numberOfGuests;
        this.position = position;
        this.status = Status.PENDING;
    }

    // Convenience methods for backward compatibility
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getCabinId() {
        return cabin != null ? cabin.getId() : null;
    }

    // Business methods
    public boolean isNotified() {
        return notifiedAt != null;
    }

    public void markAsNotified() {
        this.notifiedAt = LocalDateTime.now();
        this.status = Status.NOTIFIED;
        this.lastStatusChangeAt = this.notifiedAt;
    }

    public void markAsClaimed(Long reservationId) {
        this.claimedAt = LocalDateTime.now();
        this.claimedReservationId = reservationId;
        this.status = Status.CLAIMED;
        this.lastStatusChangeAt = this.claimedAt;
    }

    public void markAsExpired() {
        this.status = Status.EXPIRED;
        this.lastStatusChangeAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING,
        NOTIFIED,
        CLAIMED,
        EXPIRED,
        REMOVED
    }
}

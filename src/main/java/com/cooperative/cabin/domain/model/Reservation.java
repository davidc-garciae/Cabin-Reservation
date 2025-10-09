package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_user_id", columnList = "user_id"),
        @Index(name = "idx_reservation_cabin_id", columnList = "cabin_id"),
        @Index(name = "idx_reservation_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_reservation_status", columnList = "status"),
        @Index(name = "idx_reservation_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Cabaña es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabin_id", nullable = false)
    private Cabin cabin;

    @NotNull(message = "Fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Fecha de fin es obligatoria")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Número de huéspedes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Max(value = 20, message = "No puede haber más de 20 huéspedes")
    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal basePrice;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal finalPrice;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Constructors
    public Reservation(User user, Cabin cabin, LocalDate startDate, LocalDate endDate, int numberOfGuests,
            ReservationStatus status, java.math.BigDecimal basePrice, java.math.BigDecimal finalPrice) {
        this.user = user;
        this.cabin = cabin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
    }

    public Reservation(User user, Cabin cabin, LocalDate startDate, LocalDate endDate,
            LocalTime checkInTime, LocalTime checkOutTime, int numberOfGuests,
            ReservationStatus status, java.math.BigDecimal basePrice,
            java.math.BigDecimal finalPrice) {
        this.user = user;
        this.cabin = cabin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
    }

    // Business methods

    public boolean isActive() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED
                || status == ReservationStatus.IN_USE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cabins", indexes = {
        @Index(name = "idx_cabin_active", columnList = "is_active"),
        @Index(name = "idx_cabin_capacity", columnList = "capacity"),
        @Index(name = "idx_cabin_base_price", columnList = "base_price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cabin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nombre de la cabaña es obligatorio")
    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Capacidad es obligatoria")
    @Min(value = 1, message = "Capacidad debe ser al menos 1")
    @Max(value = 20, message = "Capacidad no puede exceder 20")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Número de habitaciones es obligatorio")
    @Min(value = 1, message = "Debe tener al menos 1 habitación")
    @Max(value = 10, message = "No puede tener más de 10 habitaciones")
    @Column(nullable = false)
    private Integer bedrooms;

    @NotNull(message = "Número de baños es obligatorio")
    @Min(value = 1, message = "Debe tener al menos 1 baño")
    @Max(value = 5, message = "No puede tener más de 5 baños")
    @Column(nullable = false)
    private Integer bathrooms;

    @NotNull(message = "Precio base es obligatorio")
    @DecimalMin(value = "0.01", message = "Precio base debe ser mayor a 0")
    @DecimalMax(value = "99999999.99", message = "Precio base no puede exceder 99999999.99 COP")
    @Column(name = "base_price", nullable = false, precision = 14, scale = 2)
    private java.math.BigDecimal basePrice;

    @NotNull(message = "Máximo de huéspedes es obligatorio")
    @Min(value = 1, message = "Máximo de huéspedes debe ser al menos 1")
    @Max(value = 20, message = "Máximo de huéspedes no puede exceder 20")
    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of amenities

    @Column(name = "location", columnDefinition = "TEXT")
    private String location; // JSON string with coordinates and address

    @Column(name = "default_check_in_time")
    private LocalTime defaultCheckInTime;

    @Column(name = "default_check_out_time")
    private LocalTime defaultCheckOutTime;

    // Relaciones bidireccionales
    @OneToMany(mappedBy = "cabin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "cabin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceRange> priceRanges = new ArrayList<>();

    @OneToMany(mappedBy = "cabin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AvailabilityBlock> availabilityBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "cabin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WaitingList> waitingListEntries = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors

    public Cabin(String name, String description, Integer capacity, Integer bedrooms,
            Integer bathrooms, java.math.BigDecimal basePrice, Integer maxGuests,
            String amenities, String location, LocalTime defaultCheckInTime,
            LocalTime defaultCheckOutTime) {
        this.active = true;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.amenities = amenities;
        this.location = location;
        this.defaultCheckInTime = defaultCheckInTime;
        this.defaultCheckOutTime = defaultCheckOutTime;
    }
}

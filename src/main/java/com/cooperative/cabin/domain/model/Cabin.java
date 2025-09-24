package com.cooperative.cabin.domain.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "cabins")
@EntityListeners(AuditingEntityListener.class)
public class Cabin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer bedrooms;

    @Column(nullable = false)
    private Integer bathrooms;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal basePrice;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of amenities

    @Column(name = "location", columnDefinition = "TEXT")
    private String location; // JSON string with coordinates and address

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Constructors
    public Cabin() {
        this.active = true;
    }

    public Cabin(String name, String description, Integer capacity, Integer bedrooms,
            Integer bathrooms, java.math.BigDecimal basePrice, Integer maxGuests,
            String amenities, String location) {
        this();
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.amenities = amenities;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public java.math.BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(java.math.BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

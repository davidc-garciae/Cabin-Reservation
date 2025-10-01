package com.cooperative.cabin;

import com.cooperative.cabin.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Factory para crear entidades de test de forma consistente.
 * Reemplaza el uso de constructores deprecados en los tests.
 */
public class TestEntityFactory {

    // ========== USER FACTORIES ==========

    public static User createUser(Long id, String email, String identificationNumber) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setIdentificationNumber(identificationNumber);
        user.setName("Test User " + id);
        user.setPhone("+1234567890");
        user.setPinHash("hashed-pin-" + id);
        user.setRole(User.UserRole.PROFESSOR);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User createUser(Long id, String email, String identificationNumber, User.UserRole role) {
        User user = createUser(id, email, identificationNumber);
        user.setRole(role);
        return user;
    }

    public static User createAdmin(Long id) {
        return createUser(id, "admin" + id + "@test.com", "12345678" + id, User.UserRole.ADMIN);
    }

    // ========== CABIN FACTORIES ==========

    public static Cabin createCabin(Long id, String name, int capacity) {
        Cabin cabin = new Cabin();
        cabin.setId(id);
        cabin.setName(name);
        cabin.setDescription("Test cabin " + name);
        cabin.setCapacity(capacity);
        cabin.setLocation("Test Location " + id);
        cabin.setBasePrice(BigDecimal.valueOf(100.00));
        cabin.setActive(true);
        cabin.setCreatedAt(LocalDateTime.now());
        cabin.setUpdatedAt(LocalDateTime.now());
        return cabin;
    }

    public static Cabin createCabin(Long id, String name, int capacity, BigDecimal basePrice) {
        Cabin cabin = createCabin(id, name, capacity);
        cabin.setBasePrice(basePrice);
        return cabin;
    }

    // ========== RESERVATION FACTORIES ==========

    public static Reservation createReservation(User user, Cabin cabin, LocalDate startDate,
            LocalDate endDate, int guests, ReservationStatus status) {
        return new Reservation(user, cabin, startDate, endDate, guests, status,
                BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
    }

    public static Reservation createReservation(Long id, User user, Cabin cabin, LocalDate startDate,
            LocalDate endDate, int guests, ReservationStatus status) {
        Reservation reservation = createReservation(user, cabin, startDate, endDate, guests, status);
        reservation.setId(id);
        return reservation;
    }

    public static Reservation createReservation(Long id, User user, Cabin cabin, LocalDate startDate,
            LocalDate endDate, int guests, ReservationStatus status, BigDecimal basePrice, BigDecimal finalPrice) {
        Reservation reservation = new Reservation(user, cabin, startDate, endDate, guests, status, basePrice,
                finalPrice);
        reservation.setId(id);
        return reservation;
    }

    // ========== PRICE RANGE FACTORIES ==========

    public static PriceRange createPriceRange(Cabin cabin, LocalDate startDate, LocalDate endDate,
            BigDecimal basePrice, BigDecimal multiplier, String reason, User createdBy) {
        return new PriceRange(cabin, startDate, endDate, basePrice, multiplier, reason, createdBy);
    }

    public static PriceRange createPriceRange(Long id, Cabin cabin, LocalDate startDate, LocalDate endDate,
            BigDecimal basePrice, BigDecimal multiplier, String reason, User createdBy) {
        PriceRange priceRange = createPriceRange(cabin, startDate, endDate, basePrice, multiplier, reason, createdBy);
        priceRange.setId(id);
        return priceRange;
    }

    // ========== AVAILABILITY BLOCK FACTORIES ==========

    public static AvailabilityBlock createAvailabilityBlock(Cabin cabin, LocalDate startDate,
            LocalDate endDate, String reason, User createdBy) {
        return new AvailabilityBlock(cabin, startDate, endDate, reason, createdBy);
    }

    public static AvailabilityBlock createAvailabilityBlock(Long id, Cabin cabin, LocalDate startDate,
            LocalDate endDate, String reason, User createdBy) {
        AvailabilityBlock block = createAvailabilityBlock(cabin, startDate, endDate, reason, createdBy);
        block.setId(id);
        return block;
    }

    // ========== SYSTEM CONFIGURATION FACTORIES ==========

    public static SystemConfiguration createSystemConfiguration(String configKey, String configValue) {
        return new SystemConfiguration(configKey, configValue);
    }

    public static SystemConfiguration createSystemConfiguration(String configKey, String configValue,
            String dataType, String description) {
        SystemConfiguration config = createSystemConfiguration(configKey, configValue);
        config.setDataType(dataType);
        config.setDescription(description);
        return config;
    }

    // ========== WAITING LIST FACTORIES ==========

    public static WaitingList createWaitingList(User user, Cabin cabin, LocalDate startDate,
            LocalDate endDate, int guests, int position) {
        WaitingList waitingList = new WaitingList();
        waitingList.setUser(user);
        waitingList.setCabin(cabin);
        waitingList.setRequestedStartDate(startDate);
        waitingList.setRequestedEndDate(endDate);
        waitingList.setNumberOfGuests(guests);
        waitingList.setPosition(position);
        waitingList.setStatus(WaitingList.Status.PENDING);
        waitingList.setCreatedAt(LocalDateTime.now());
        waitingList.setLastStatusChangeAt(LocalDateTime.now());
        return waitingList;
    }

    public static WaitingList createWaitingList(Long id, User user, Cabin cabin, LocalDate startDate,
            LocalDate endDate, int guests, int position, WaitingList.Status status) {
        WaitingList waitingList = createWaitingList(user, cabin, startDate, endDate, guests, position);
        waitingList.setId(id);
        waitingList.setStatus(status);
        return waitingList;
    }

    // ========== PASSWORD RESET TOKEN FACTORIES ==========

    public static PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime expiresAt) {
        return new PasswordResetToken(token, user, expiresAt);
    }

    // ========== AUDIT LOG FACTORIES ==========

    public static AuditLog createAuditLog(String action, String entityType, Long entityId,
            String oldValues, String newValues, User user, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);
        auditLog.setUser(user);
        auditLog.setIpAddress(ipAddress);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setUpdatedAt(LocalDateTime.now());
        return auditLog;
    }

    // ========== PRICE CHANGE HISTORY FACTORIES ==========

    public static PriceChangeHistory createPriceChangeHistory(PriceRange priceRange, BigDecimal oldPrice,
            BigDecimal newPrice, String changeReason, User changedBy) {
        PriceChangeHistory history = new PriceChangeHistory();
        history.setPriceRange(priceRange);
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setChangeReason(changeReason);
        history.setChangedBy(changedBy);
        history.setCreatedAt(LocalDateTime.now());
        return history;
    }

    // ========== DOCUMENT NUMBER FACTORIES ==========

    public static DocumentNumber createDocumentNumber(String documentNumber, DocumentNumber.DocumentStatus status) {
        return new DocumentNumber(documentNumber, status);
    }

    // ========== CONVENIENCE METHODS ==========

    /**
     * Crea un conjunto completo de entidades relacionadas para tests
     */
    public static TestDataBundle createTestDataBundle(Long userId, Long cabinId) {
        User user = createUser(userId, "user" + userId + "@test.com", "12345678" + userId);
        Cabin cabin = createCabin(cabinId, "Test Cabin " + cabinId, 4);
        return new TestDataBundle(user, cabin);
    }

    /**
     * Bundle que contiene entidades relacionadas para tests
     */
    public static class TestDataBundle {
        public final User user;
        public final Cabin cabin;

        public TestDataBundle(User user, Cabin cabin) {
            this.user = user;
            this.cabin = cabin;
        }

        public Reservation createReservation(LocalDate startDate, LocalDate endDate, int guests,
                ReservationStatus status) {
            return TestEntityFactory.createReservation(user, cabin, startDate, endDate, guests, status);
        }

        public PriceRange createPriceRange(LocalDate startDate, LocalDate endDate, BigDecimal basePrice,
                BigDecimal multiplier) {
            return TestEntityFactory.createPriceRange(cabin, startDate, endDate, basePrice, multiplier, "Test reason",
                    user);
        }

        public AvailabilityBlock createAvailabilityBlock(LocalDate startDate, LocalDate endDate, String reason) {
            return TestEntityFactory.createAvailabilityBlock(cabin, startDate, endDate, reason, user);
        }
    }
}

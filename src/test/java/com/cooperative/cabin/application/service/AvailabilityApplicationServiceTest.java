package com.cooperative.cabin.application.service;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.AvailabilityBlockJpaRepository;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import com.cooperative.cabin.infrastructure.repository.ReservationJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityApplicationServiceTest {

    @Mock
    private ReservationJpaRepository reservationRepository;

    @Mock
    private AvailabilityBlockJpaRepository availabilityBlockRepository;

    @Mock
    private CabinJpaRepository cabinRepository;

    @InjectMocks
    private AvailabilityApplicationServiceImpl availabilityService;

    @Test
    void getAvailableDates_shouldReturnAvailableDatesFromAllActiveCabins() {
        // Given
        Cabin cabin1 = TestEntityFactory.createCabin(1L, "Cabin 1", 4);
        List<Cabin> activeCabins = List.of(cabin1);

        when(cabinRepository.findByActiveTrue()).thenReturn(activeCabins);
        when(reservationRepository.isCabinReservedOnDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(eq(1L), any(LocalDate.class))).thenReturn(false);

        // When
        List<String> result = availabilityService.getAvailableDates();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(date -> date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void getAvailableDates_shouldReturnEmptyList_whenNoActiveCabins() {
        // Given
        when(cabinRepository.findByActiveTrue()).thenReturn(List.of());

        // When
        List<String> result = availabilityService.getAvailableDates();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getAvailabilityCalendar_shouldReturnCalendarFromAllActiveCabins() {
        // Given
        Cabin cabin1 = TestEntityFactory.createCabin(1L, "Cabin 1", 4);
        List<Cabin> activeCabins = List.of(cabin1);

        when(cabinRepository.findByActiveTrue()).thenReturn(activeCabins);
        when(reservationRepository.isCabinReservedOnDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(eq(1L), any(LocalDate.class))).thenReturn(false);

        // When
        Map<String, Boolean> result = availabilityService.getAvailabilityCalendar(2025, 2);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.keySet()).allMatch(date -> date.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertThat(result.values()).contains(true); // At least some dates should be available
    }

    @Test
    void getAvailabilityCalendar_shouldReturnAllFalse_whenNoActiveCabins() {
        // Given
        when(cabinRepository.findByActiveTrue()).thenReturn(List.of());

        // When
        Map<String, Boolean> result = availabilityService.getAvailabilityCalendar(2025, 2);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.values()).containsOnly(false); // All dates should be unavailable
    }

    @Test
    void getAvailableDatesForCabin_shouldReturnAvailableDates_whenCabinExistsAndActive() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.isCabinReservedOnDate(eq(cabinId), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(eq(cabinId), any(LocalDate.class))).thenReturn(false);

        // When
        List<String> result = availabilityService.getAvailableDatesForCabin(cabinId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(date -> date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void getAvailableDatesForCabin_shouldThrowException_whenCabinNotFound() {
        // Given
        Long cabinId = 999L;
        when(cabinRepository.findById(cabinId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> availabilityService.getAvailableDatesForCabin(cabinId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Cabaña no encontrada");
    }

    @Test
    void getAvailableDatesForCabin_shouldThrowException_whenCabinInactive() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        cabin.setActive(false);
        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));

        // When & Then
        assertThatThrownBy(() -> availabilityService.getAvailableDatesForCabin(cabinId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cabaña no está activa");
    }

    @Test
    void getAvailabilityCalendarForCabin_shouldReturnCalendar_whenCabinExistsAndActive() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.isCabinReservedOnDate(eq(cabinId), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(eq(cabinId), any(LocalDate.class))).thenReturn(false);

        // When
        Map<String, Boolean> result = availabilityService.getAvailabilityCalendarForCabin(cabinId, 2025, 2);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.keySet()).allMatch(date -> date.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertThat(result.values()).contains(true); // At least some dates should be available
    }

    @Test
    void isCabinAvailable_shouldReturnTrue_whenNoReservationsOrBlocks() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.findActiveReservationsByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of());
        when(availabilityBlockRepository.findByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of());

        // When
        boolean result = availabilityService.isCabinAvailable(cabinId, startDate, endDate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isCabinAvailable_shouldReturnFalse_whenReservationExists() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        User user = TestEntityFactory.createUser(1L, "user@test.com", "12345678");
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        Reservation reservation = TestEntityFactory.createReservation(
                user, cabin, startDate, endDate, 2, ReservationStatus.CONFIRMED);

        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.findActiveReservationsByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of(reservation));

        // When
        boolean result = availabilityService.isCabinAvailable(cabinId, startDate, endDate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isCabinAvailable_shouldReturnFalse_whenCabinInactive() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        cabin.setActive(false);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));

        // When
        boolean result = availabilityService.isCabinAvailable(cabinId, startDate, endDate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isCabinAvailable_shouldReturnTrue_whenAvailabilityBlockMatchesExactly() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        // Create a block that matches exactly the requested range
        AvailabilityBlock block = TestEntityFactory.createAvailabilityBlock(
                cabin, startDate, endDate, "Maintenance", admin);

        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.findActiveReservationsByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of());
        when(availabilityBlockRepository.findByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of(block));

        // When
        boolean result = availabilityService.isCabinAvailable(cabinId, startDate, endDate);

        // Then
        // According to AvailabilityPolicies.respectsMandatoryBlockRanges, if the block
        // matches exactly,
        // it should return true (allowing the reservation)
        assertThat(result).isTrue();
    }

    @Test
    void isCabinAvailable_shouldReturnFalse_whenAvailabilityBlockPartiallyOverlaps() {
        // Given
        Long cabinId = 1L;
        Cabin cabin = TestEntityFactory.createCabin(cabinId, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        // Create a block that partially overlaps with the requested range
        AvailabilityBlock block = TestEntityFactory.createAvailabilityBlock(
                cabin, startDate.plusDays(1), endDate.plusDays(1), "Maintenance", admin);

        when(cabinRepository.findById(cabinId)).thenReturn(Optional.of(cabin));
        when(reservationRepository.findActiveReservationsByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of());
        when(availabilityBlockRepository.findByCabinAndDateRange(cabinId, startDate, endDate))
                .thenReturn(List.of(block));

        // When
        boolean result = availabilityService.isCabinAvailable(cabinId, startDate, endDate);

        // Then
        // The block partially overlaps, so it should return false
        assertThat(result).isFalse();
    }

    @Test
    void getAvailableDatesInRange_shouldReturnAvailableDates() {
        // Given
        Long cabinId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        when(reservationRepository.isCabinReservedOnDate(eq(cabinId), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(eq(cabinId), any(LocalDate.class))).thenReturn(false);

        // When
        List<LocalDate> result = availabilityService.getAvailableDatesInRange(cabinId, startDate, endDate);

        // Then
        assertThat(result).hasSize(3); // 3 days in range
        assertThat(result).contains(startDate, startDate.plusDays(1), startDate.plusDays(2));
    }

    @Test
    void getAvailableDatesInRange_shouldExcludeReservedDates() {
        // Given
        Long cabinId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        when(reservationRepository.isCabinReservedOnDate(cabinId, startDate)).thenReturn(false);
        when(reservationRepository.isCabinReservedOnDate(cabinId, startDate.plusDays(1))).thenReturn(true); // Reserved
        when(reservationRepository.isCabinReservedOnDate(cabinId, startDate.plusDays(2))).thenReturn(false);

        when(availabilityBlockRepository.isDateBlocked(eq(cabinId), any(LocalDate.class))).thenReturn(false);

        // When
        List<LocalDate> result = availabilityService.getAvailableDatesInRange(cabinId, startDate, endDate);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(startDate, startDate.plusDays(2));
        assertThat(result).doesNotContain(startDate.plusDays(1));
    }

    @Test
    void getAvailableDatesInRange_shouldExcludeBlockedDates() {
        // Given
        Long cabinId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        when(reservationRepository.isCabinReservedOnDate(eq(cabinId), any(LocalDate.class))).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(cabinId, startDate)).thenReturn(false);
        when(availabilityBlockRepository.isDateBlocked(cabinId, startDate.plusDays(1))).thenReturn(true); // Blocked
        when(availabilityBlockRepository.isDateBlocked(cabinId, startDate.plusDays(2))).thenReturn(false);

        // When
        List<LocalDate> result = availabilityService.getAvailableDatesInRange(cabinId, startDate, endDate);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(startDate, startDate.plusDays(2));
        assertThat(result).doesNotContain(startDate.plusDays(1));
    }
}

package com.cooperative.cabin.application.service;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.PriceRangeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingApplicationServiceTest {

    @Mock
    private PriceRangeJpaRepository repository;

    @InjectMocks
    private PricingApplicationServiceImpl pricingService;

    @Test
    void getCalendar_shouldReturnPricesForEachDay() {
        // Given
        Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);

        // Crear rango de precios para marzo 2024
        PriceRange range1 = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 15),
                new BigDecimal("100.00"),
                new BigDecimal("1.5"),
                "Temporada alta",
                admin);

        PriceRange range2 = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 3, 16),
                LocalDate.of(2024, 3, 31),
                new BigDecimal("100.00"),
                new BigDecimal("2.0"),
                "Temporada muy alta",
                admin);

        when(repository.findAll()).thenReturn(List.of(range1, range2));

        // When
        Map<String, BigDecimal> calendar = pricingService.getCalendar(2024, 3);

        // Then
        assertThat(calendar).hasSize(31); // Marzo tiene 31 días

        // Verificar precios en la primera mitad del mes
        assertThat(calendar.get("2024-03-01")).isEqualByComparingTo(new BigDecimal("150.00")); // 100 * 1.5
        assertThat(calendar.get("2024-03-15")).isEqualByComparingTo(new BigDecimal("150.00")); // 100 * 1.5

        // Verificar precios en la segunda mitad del mes
        assertThat(calendar.get("2024-03-16")).isEqualByComparingTo(new BigDecimal("200.00")); // 100 * 2.0
        assertThat(calendar.get("2024-03-31")).isEqualByComparingTo(new BigDecimal("200.00")); // 100 * 2.0
    }

    @Test
    void getCalendar_shouldReturnZeroForDaysWithoutPriceRange() {
        // Given
        when(repository.findAll()).thenReturn(List.of());

        // When
        Map<String, BigDecimal> calendar = pricingService.getCalendar(2024, 3);

        // Then
        assertThat(calendar).hasSize(31);
        assertThat(calendar.values()).allMatch(price -> price.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void getHistory_shouldReturnOrderedHistory() {
        // Given
        Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);

        PriceRange range1 = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                "Precio normal",
                admin);
        range1.setId(1L);
        range1.setCreatedAt(java.time.LocalDateTime.of(2024, 1, 1, 10, 0)); // Más antiguo

        PriceRange range2 = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 29),
                new BigDecimal("100.00"),
                new BigDecimal("1.5"),
                "Temporada alta",
                admin);
        range2.setId(2L);
        range2.setCreatedAt(java.time.LocalDateTime.of(2024, 2, 1, 10, 0)); // Más reciente

        when(repository.findAll()).thenReturn(List.of(range1, range2));

        // When
        List<Map<String, Object>> history = pricingService.getHistory();

        // Then
        assertThat(history).hasSize(2);

        // Verificar que contiene ambos rangos
        assertThat(history).extracting("id").containsExactlyInAnyOrder(1L, 2L);
        assertThat(history).extracting("cabinId").containsOnly(1L);
        assertThat(history).extracting("cabinName").containsOnly("Test Cabin");
        assertThat(history).extracting("basePrice").containsOnly(new BigDecimal("100.00"));

        // Verificar que los precios finales son correctos
        assertThat(history).extracting("finalPrice").containsExactlyInAnyOrder(
                new BigDecimal("100.000"), // 100 * 1.0
                new BigDecimal("150.000") // 100 * 1.5
        );

        // Verificar que las razones son correctas
        assertThat(history).extracting("reason").containsExactlyInAnyOrder("Precio normal", "Temporada alta");
    }

    @Test
    void getHistory_shouldHandleNullCreatedBy() {
        // Given
        Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);

        PriceRange range = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                "Precio normal",
                null // Sin creador
        );
        range.setId(1L);
        range.setCreatedAt(java.time.LocalDateTime.of(2024, 1, 1, 10, 0));

        when(repository.findAll()).thenReturn(List.of(range));

        // When
        List<Map<String, Object>> history = pricingService.getHistory();

        // Then
        assertThat(history).hasSize(1);
        Map<String, Object> entry = history.get(0);
        assertThat(entry.get("createdBy")).isEqualTo("Sistema");
    }

    @Test
    void calculatePrice_shouldReturnCorrectPrice() {
        // Given
        Cabin cabin = TestEntityFactory.createCabin(1L, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);

        PriceRange range = TestEntityFactory.createPriceRange(
                cabin,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 31),
                new BigDecimal("100.00"),
                new BigDecimal("1.5"),
                "Temporada alta",
                admin);

        when(repository.findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(1L, LocalDate.of(2024, 3, 15),
                LocalDate.of(2024, 3, 15)))
                .thenReturn(List.of(range));

        // When
        BigDecimal price = pricingService.calculatePrice(1L, LocalDate.of(2024, 3, 15));

        // Then
        assertThat(price).isEqualByComparingTo(new BigDecimal("150.00")); // 100 * 1.5
    }

    @Test
    void calculatePrice_shouldReturnZeroWhenNoRangeFound() {
        // Given
        when(repository.findByCabin_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(1L, LocalDate.of(2024, 3, 15),
                LocalDate.of(2024, 3, 15)))
                .thenReturn(List.of());

        // When
        BigDecimal price = pricingService.calculatePrice(1L, LocalDate.of(2024, 3, 15));

        // Then
        assertThat(price).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

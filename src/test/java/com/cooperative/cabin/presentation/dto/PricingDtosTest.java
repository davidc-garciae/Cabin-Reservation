package com.cooperative.cabin.presentation.dto;

import com.cooperative.cabin.TestEntityFactory;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.presentation.mapper.PriceRangeMapper;
import com.cooperative.cabin.presentation.controller.PricingAdminController.CreatePriceRangeRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PricingDtosTest {

    @Test
    void mapsDomainToResponse() {
        Cabin cabin = TestEntityFactory.createCabin(2L, "Test Cabin", 4);
        User admin = TestEntityFactory.createAdmin(1L);
        PriceRange pr = TestEntityFactory.createPriceRange(cabin, LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 31), new BigDecimal("130.00"), new BigDecimal("1.15"),
                "Test reason", admin);
        pr.setId(9L);
        PriceRangeResponse dto = PriceRangeMapper.INSTANCE.toResponse(pr);

        assertThat(dto.getId()).isEqualTo(9L);
        assertThat(dto.getCabinId()).isEqualTo(2L);
        assertThat(dto.getStartDate()).isEqualTo("2025-03-01");
        assertThat(dto.getEndDate()).isEqualTo("2025-03-31");
        assertThat(dto.getBasePrice()).isEqualTo("130.00");
        assertThat(dto.getMultiplier()).isEqualTo("1.15");
    }

    @Test
    void mapsCreateRequestToDomain() {
        CreatePriceRangeRequest req = new CreatePriceRangeRequest();
        req.setCabinId(2L);
        req.setStartDate(LocalDate.parse("2025-03-01"));
        req.setEndDate(LocalDate.parse("2025-03-31"));
        req.setBasePrice(new BigDecimal("130.00"));
        req.setMultiplier(new BigDecimal("1.15"));
        
        System.out.println("DEBUG: Request object: " + req);
        System.out.println("DEBUG: CabinId: " + req.getCabinId());
        System.out.println("DEBUG: StartDate: " + req.getStartDate());
        System.out.println("DEBUG: EndDate: " + req.getEndDate());
        System.out.println("DEBUG: BasePrice: " + req.getBasePrice());
        System.out.println("DEBUG: Multiplier: " + req.getMultiplier());
        
        PriceRangeMapper mapper = Mappers.getMapper(PriceRangeMapper.class);
        System.out.println("DEBUG: Mapper instance: " + mapper);
        
        PriceRange pr = mapper.fromCreateRequest(req);
        System.out.println("DEBUG: Result PriceRange: " + pr);
        
        if (pr != null) {
            System.out.println("DEBUG: Cabin: " + pr.getCabin());
            System.out.println("DEBUG: Cabin ID: " + (pr.getCabin() != null ? pr.getCabin().getId() : "null"));
            System.out.println("DEBUG: StartDate: " + pr.getStartDate());
            System.out.println("DEBUG: EndDate: " + pr.getEndDate());
            System.out.println("DEBUG: BasePrice: " + pr.getBasePrice());
            System.out.println("DEBUG: PriceMultiplier: " + pr.getPriceMultiplier());
        }
        
        assertThat(pr).isNotNull();
        assertThat(pr.getCabin().getId()).isEqualTo(2L);
        assertThat(pr.getStartDate()).isEqualTo(LocalDate.parse("2025-03-01"));
        assertThat(pr.getEndDate()).isEqualTo(LocalDate.parse("2025-03-31"));
        assertThat(pr.getBasePrice()).isEqualByComparingTo(new BigDecimal("130.00"));
        assertThat(pr.getPriceMultiplier()).isEqualByComparingTo(new BigDecimal("1.15"));
    }
}

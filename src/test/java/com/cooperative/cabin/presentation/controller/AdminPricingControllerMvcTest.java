package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.PricingApplicationService;
import com.cooperative.cabin.domain.model.PriceRange;
import com.cooperative.cabin.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = PricingAdminController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminPricingControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PricingApplicationService pricingApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void patchPriceRange_updatesFieldsPartially() throws Exception {
                PriceRange updated = new PriceRange(5L, 1L,
                                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28),
                                new BigDecimal("120.00"), new BigDecimal("1.10"));

                given(pricingApplicationService.partialUpdatePriceRange(
                                eq(5L), eq(new BigDecimal("120.00")), eq(new BigDecimal("1.10")), eq(null), eq(null)))
                                .willReturn(updated);

                String body = "{" +
                                "\"basePrice\":\"120.00\"," +
                                "\"multiplier\":\"1.10\"" +
                                "}";

                mockMvc.perform(patch("/api/admin/pricing/ranges/{id}", 5).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(5))
                                .andExpect(jsonPath("$.basePrice").value(120.00))
                                .andExpect(jsonPath("$.multiplier").value(1.10));
        }

        @Test
        void getPriceRanges_returnsList() throws Exception {
                given(pricingApplicationService.listPriceRanges())
                                .willReturn(java.util.List.of(
                                                new PriceRange(1L, 1L, java.time.LocalDate.of(2025, 1, 1),
                                                                java.time.LocalDate.of(2025, 1, 31),
                                                                new java.math.BigDecimal("100.00"),
                                                                java.math.BigDecimal.ONE)));

                mockMvc.perform(get("/api/admin/pricing/ranges"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void postPriceRange_createsRange() throws Exception {
                PriceRange created = new PriceRange(9L, 1L, java.time.LocalDate.of(2025, 3, 1),
                                java.time.LocalDate.of(2025, 3, 31), new java.math.BigDecimal("130.00"),
                                new java.math.BigDecimal("1.15"));
                given(pricingApplicationService.createPriceRange(
                                eq(1L), eq(java.time.LocalDate.of(2025, 3, 1)), eq(java.time.LocalDate.of(2025, 3, 31)),
                                eq(new java.math.BigDecimal("130.00")), eq(new java.math.BigDecimal("1.15"))))
                                .willReturn(created);

                String body = "{" +
                                "\"cabinId\":1," +
                                "\"startDate\":\"2025-03-01\"," +
                                "\"endDate\":\"2025-03-31\"," +
                                "\"basePrice\":\"130.00\"," +
                                "\"multiplier\":\"1.15\"" +
                                "}";

                mockMvc.perform(post("/api/admin/pricing/ranges").with(csrf())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(9));
        }

        @Test
        void putPriceRange_updatesRange() throws Exception {
                PriceRange updated = new PriceRange(9L, 1L, java.time.LocalDate.of(2025, 3, 1),
                                java.time.LocalDate.of(2025, 3, 31), new java.math.BigDecimal("150.00"),
                                new java.math.BigDecimal("1.20"));
                given(pricingApplicationService.updatePriceRange(
                                eq(9L), eq(1L), eq(java.time.LocalDate.of(2025, 3, 1)),
                                eq(java.time.LocalDate.of(2025, 3, 31)), eq(new java.math.BigDecimal("150.00")),
                                eq(new java.math.BigDecimal("1.20")))).willReturn(updated);

                String body = "{" +
                                "\"cabinId\":1," +
                                "\"startDate\":\"2025-03-01\"," +
                                "\"endDate\":\"2025-03-31\"," +
                                "\"basePrice\":\"150.00\"," +
                                "\"multiplier\":\"1.20\"" +
                                "}";

                mockMvc.perform(put("/api/admin/pricing/ranges/{id}", 9).with(csrf())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(9))
                                .andExpect(jsonPath("$.basePrice").value(150.00));
        }

        @Test
        void deletePriceRange_removesRange() throws Exception {
                org.mockito.BDDMockito.willDoNothing().given(pricingApplicationService).deletePriceRange(eq(9L));

                mockMvc.perform(delete("/api/admin/pricing/ranges/{id}", 9).with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        void getCalendar_returnsDaysMap() throws Exception {
                given(pricingApplicationService.getCalendar(2025, 2))
                                .willReturn(java.util.Map.of(
                                                "2025-02-01", new java.math.BigDecimal("120.00"),
                                                "2025-02-02", new java.math.BigDecimal("130.00")));

                mockMvc.perform(get("/api/admin/pricing/calendar/{year}/{month}", 2025, 2))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.['2025-02-01']").value(120.00))
                                .andExpect(jsonPath("$.['2025-02-02']").value(130.00));
        }

        @Test
        void getHistory_returnsArray() throws Exception {
                given(pricingApplicationService.getHistory())
                                .willReturn(java.util.List.of(
                                                java.util.Map.of(
                                                                "id", 1,
                                                                "cabinId", 1,
                                                                "date", "2025-02-01",
                                                                "oldPrice", new java.math.BigDecimal("110.00"),
                                                                "newPrice", new java.math.BigDecimal("120.00"))));

                mockMvc.perform(get("/api/admin/pricing/history"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].newPrice").value(120.00));
        }

        @Test
        void calculate_returnsPrice() throws Exception {
                given(pricingApplicationService.calculatePrice(1L, java.time.LocalDate.of(2025, 2, 1)))
                                .willReturn(new java.math.BigDecimal("123.45"));

                mockMvc.perform(get("/api/admin/pricing/calculate")
                                .param("cabinId", "1")
                                .param("date", "2025-02-01"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.price").value(123.45));
        }
}

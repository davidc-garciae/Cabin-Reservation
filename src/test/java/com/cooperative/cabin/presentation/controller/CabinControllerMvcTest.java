package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.CabinApplicationService;
import com.cooperative.cabin.domain.exception.CabinNotFoundException;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CabinController.class)
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CabinControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CabinApplicationService cabinApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void getAllCabins_returnsListOfCabins() throws Exception {
                CabinResponse cabin1 = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));
                CabinResponse cabin2 = new CabinResponse(
                                2L, "Cabaña del Bosque", "Cabaña en el bosque", 4, 2, 1,
                                BigDecimal.valueOf(120.00), 4, true, "{\"parking\": true}",
                                "{\"address\": \"Bosque 456\"}",
                                LocalDateTime.parse("2024-01-16T11:00:00"), LocalDateTime.parse("2024-01-21T15:00:00"));

                when(cabinApplicationService.getAllActiveCabins()).thenReturn(List.of(cabin1, cabin2));

                mockMvc.perform(get("/api/cabins")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Cabaña del Lago"))
                                .andExpect(jsonPath("$[0].capacity").value(6))
                                .andExpect(jsonPath("$[0].basePrice").value(150.00))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].name").value("Cabaña del Bosque"));
        }

        @Test
        void getCabinById_returnsCabin() throws Exception {
                CabinResponse cabin = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));

                when(cabinApplicationService.getCabinById(1L)).thenReturn(cabin);

                mockMvc.perform(get("/api/cabins/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Cabaña del Lago"))
                                .andExpect(jsonPath("$.capacity").value(6))
                                .andExpect(jsonPath("$.basePrice").value(150.00));
        }

        @Test
        void getCabinById_notFound_returns404() throws Exception {
                when(cabinApplicationService.getCabinById(999L))
                                .thenThrow(new CabinNotFoundException("Cabin not found with id: 999"));

                mockMvc.perform(get("/api/cabins/999")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void searchCabins_withMinCapacity_returnsFilteredCabins() throws Exception {
                CabinResponse cabin = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));

                when(cabinApplicationService.searchCabins(eq(4), any(), any(), any()))
                                .thenReturn(List.of(cabin));

                mockMvc.perform(get("/api/cabins/search")
                                .param("minCapacity", "4")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].capacity").value(6));
        }

        @Test
        void searchCabins_withPriceRange_returnsFilteredCabins() throws Exception {
                CabinResponse cabin = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));

                when(cabinApplicationService.searchCabins(any(), eq(BigDecimal.valueOf(100)),
                                eq(BigDecimal.valueOf(200)),
                                any()))
                                .thenReturn(List.of(cabin));

                mockMvc.perform(get("/api/cabins/search")
                                .param("minPrice", "100")
                                .param("maxPrice", "200")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].basePrice").value(150.00));
        }

        @Test
        void searchCabins_withName_returnsFilteredCabins() throws Exception {
                CabinResponse cabin = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));

                when(cabinApplicationService.searchCabins(any(), any(), any(), eq("lago")))
                                .thenReturn(List.of(cabin));

                mockMvc.perform(get("/api/cabins/search")
                                .param("name", "lago")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Cabaña del Lago"));
        }
}

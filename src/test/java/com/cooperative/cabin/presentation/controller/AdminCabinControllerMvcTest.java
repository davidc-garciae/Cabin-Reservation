package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.CabinApplicationService;
import com.cooperative.cabin.domain.exception.CabinNotFoundException;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCabinController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminCabinControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CabinApplicationService cabinApplicationService;

        @MockBean
        private JwtService jwtService;

        @Test
        void getAllCabins_returnsAllCabins() throws Exception {
                CabinResponse activeCabin = new CabinResponse(
                                1L, "Cabaña Activa", "Cabaña activa", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}",
                                "{\"address\": \"Activa 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));
                CabinResponse inactiveCabin = new CabinResponse(
                                2L, "Cabaña Inactiva", "Cabaña inactiva", 4, 2, 1,
                                BigDecimal.valueOf(120.00), 4, false, "{\"parking\": true}",
                                "{\"address\": \"Inactiva 456\"}",
                                LocalDateTime.parse("2024-01-16T11:00:00"), LocalDateTime.parse("2024-01-21T15:00:00"));

                when(cabinApplicationService.getAllCabinsForAdmin()).thenReturn(List.of(activeCabin, inactiveCabin));

                mockMvc.perform(get("/api/admin/cabins")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].active").value(false));
        }

        @Test
        void getCabinById_returnsCabin() throws Exception {
                CabinResponse cabin = new CabinResponse(
                                1L, "Cabaña del Lago", "Hermosa cabaña con vista al lago", 6, 3, 2,
                                BigDecimal.valueOf(150.00), 6, true, "{\"wifi\": true}", "{\"address\": \"Lago 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-20T14:45:00"));

                when(cabinApplicationService.getCabinByIdForAdmin(1L)).thenReturn(cabin);

                mockMvc.perform(get("/api/admin/cabins/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Cabaña del Lago"));
        }

        @Test
        void getCabinById_notFound_returns404() throws Exception {
                when(cabinApplicationService.getCabinByIdForAdmin(999L))
                                .thenThrow(new CabinNotFoundException("Cabin not found with id: 999"));

                mockMvc.perform(get("/api/admin/cabins/999")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void createCabin_returnsCreatedCabin() throws Exception {
                CreateCabinRequest request = new CreateCabinRequest(
                                "Nueva Cabaña", "Descripción de la nueva cabaña", 4, 2, 1,
                                BigDecimal.valueOf(120.00), 4, "{\"wifi\": true}", "{\"address\": \"Nueva 789\"}");

                CabinResponse createdCabin = new CabinResponse(
                                3L, "Nueva Cabaña", "Descripción de la nueva cabaña", 4, 2, 1,
                                BigDecimal.valueOf(120.00), 4, true, "{\"wifi\": true}", "{\"address\": \"Nueva 789\"}",
                                LocalDateTime.parse("2024-01-25T10:00:00"), LocalDateTime.parse("2024-01-25T10:00:00"));

                when(cabinApplicationService.createCabin(any(CreateCabinRequest.class))).thenReturn(createdCabin);

                mockMvc.perform(post("/api/admin/cabins")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(3))
                                .andExpect(jsonPath("$.name").value("Nueva Cabaña"))
                                .andExpect(jsonPath("$.capacity").value(4));
        }

        @Test
        void createCabin_invalidData_returns400() throws Exception {
                CreateCabinRequest invalidRequest = new CreateCabinRequest(
                                "", // Empty name should fail validation
                                "Descripción", 4, 2, 1,
                                BigDecimal.valueOf(120.00), 4, "{\"wifi\": true}", "{\"address\": \"Nueva 789\"}");

                mockMvc.perform(post("/api/admin/cabins")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                                .with(csrf()))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateCabin_returnsUpdatedCabin() throws Exception {
                UpdateCabinRequest request = new UpdateCabinRequest(
                                "Cabaña Actualizada", "Nueva descripción", 8, 4, 3,
                                BigDecimal.valueOf(180.00), 8, true, "{\"wifi\": true, \"pool\": true}",
                                "{\"address\": \"Actualizada 123\"}");

                CabinResponse updatedCabin = new CabinResponse(
                                1L, "Cabaña Actualizada", "Nueva descripción", 8, 4, 3,
                                BigDecimal.valueOf(180.00), 8, true, "{\"wifi\": true, \"pool\": true}",
                                "{\"address\": \"Actualizada 123\"}",
                                LocalDateTime.parse("2024-01-15T10:30:00"), LocalDateTime.parse("2024-01-25T16:00:00"));

                when(cabinApplicationService.updateCabin(eq(1L), any(UpdateCabinRequest.class)))
                                .thenReturn(updatedCabin);

                mockMvc.perform(put("/api/admin/cabins/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Cabaña Actualizada"))
                                .andExpect(jsonPath("$.capacity").value(8))
                                .andExpect(jsonPath("$.basePrice").value(180.00));
        }

        @Test
        void updateCabin_notFound_returns404() throws Exception {
                UpdateCabinRequest request = new UpdateCabinRequest(
                                "Cabaña Actualizada", null, null, null, null,
                                null, null, null, null, null);

                when(cabinApplicationService.updateCabin(eq(999L), any(UpdateCabinRequest.class)))
                                .thenThrow(new CabinNotFoundException("Cabin not found with id: 999"));

                mockMvc.perform(put("/api/admin/cabins/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        void deleteCabin_returnsNoContent() throws Exception {
                doNothing().when(cabinApplicationService).deleteCabin(1L);

                mockMvc.perform(delete("/api/admin/cabins/1")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        void deleteCabin_notFound_returns404() throws Exception {
                willThrow(new CabinNotFoundException("Cabin not found with id: 999"))
                                .given(cabinApplicationService).deleteCabin(999L);

                mockMvc.perform(delete("/api/admin/cabins/999")
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }
}

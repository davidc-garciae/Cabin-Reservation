package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AvailabilityBlocksAdminService;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = AdminAvailabilityBlocksController.class)
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminAvailabilityBlocksControllerMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AvailabilityBlocksAdminService service;

        @MockBean
        private JwtService jwtService;

        @Test
        void listBlocks_returnsList() throws Exception {
                given(service.list())
                                .willReturn(List.of(new AvailabilityBlock(1L, 1L, LocalDate.of(2025, 4, 1),
                                                LocalDate.of(2025, 4, 4))));
                mockMvc.perform(get("/api/admin/availability/blocks"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        void createBlock_returnsCreated() throws Exception {
                AvailabilityBlock created = new AvailabilityBlock(10L, 2L, LocalDate.of(2025, 5, 1),
                                LocalDate.of(2025, 5, 3));
                given(service.create(eq(2L), eq(LocalDate.of(2025, 5, 1)), eq(LocalDate.of(2025, 5, 3))))
                                .willReturn(created);

                String body = "{" +
                                "\"cabinId\":2," +
                                "\"startDate\":\"2025-05-01\"," +
                                "\"endDate\":\"2025-05-03\"" +
                                "}";

                mockMvc.perform(post("/api/admin/availability/blocks").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(10));
        }

        @Test
        void updateBlock_returnsUpdated() throws Exception {
                AvailabilityBlock updated = new AvailabilityBlock(10L, 3L, LocalDate.of(2025, 6, 1),
                                LocalDate.of(2025, 6, 5));
                given(service.update(eq(10L), eq(3L), eq(LocalDate.of(2025, 6, 1)), eq(LocalDate.of(2025, 6, 5))))
                                .willReturn(updated);

                String body = "{" +
                                "\"cabinId\":3," +
                                "\"startDate\":\"2025-06-01\"," +
                                "\"endDate\":\"2025-06-05\"" +
                                "}";

                mockMvc.perform(put("/api/admin/availability/blocks/{id}", 10).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.cabinId").value(3));
        }

        @Test
        void deleteBlock_returnsNoContent() throws Exception {
                willDoNothing().given(service).delete(eq(10L));
                mockMvc.perform(delete("/api/admin/availability/blocks/{id}", 10).with(csrf()))
                                .andExpect(status().isNoContent());
        }
}

package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.AdminDocumentNumberApplicationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.CreateDocumentNumberRequest;
import com.cooperative.cabin.presentation.dto.DocumentNumberResponse;
import com.cooperative.cabin.presentation.dto.UpdateDocumentNumberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDocumentNumberController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminDocumentNumberControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminDocumentNumberApplicationService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldListDocuments() throws Exception {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        when(service.getAllDocuments()).thenReturn(List.of(new DocumentNumberResponse(1L, "123", "ACTIVE", now, now)));

        mockMvc.perform(get("/api/admin/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldReturnPagedDocuments() throws Exception {
        var page = new PageImpl<>(List.of(new DocumentNumberResponse(1L, "123", "ACTIVE",
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now())), PageRequest.of(0, 1), 1);
        when(service.getAllDocumentsPaged(any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/documents/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].documentNumber").value("123"));
    }

    @Test
    void shouldCreateDocument() throws Exception {
        when(service.createDocument(any(CreateDocumentNumberRequest.class)))
                .thenReturn(new DocumentNumberResponse(1L, "1234567", "ACTIVE",
                        java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(post("/api/admin/documents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDocumentNumberRequest("1234567", "ACTIVE"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldUpdateDocument() throws Exception {
        when(service.updateDocument(any(), any(UpdateDocumentNumberRequest.class)))
                .thenReturn(new DocumentNumberResponse(1L, "123", "DISABLED",
                        java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(put("/api/admin/documents/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateDocumentNumberRequest("DISABLED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
    }
}


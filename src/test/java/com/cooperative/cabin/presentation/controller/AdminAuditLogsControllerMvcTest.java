package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.CabinReservationApplication;
import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.AuditLogResponse;
import com.cooperative.cabin.presentation.dto.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuditLogsController.class)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class AdminAuditLogsControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AdminAuditLogsService adminAuditLogsService;

    @Test
    void listAuditLogs_returnsPage() throws Exception {
        AuditLogResponse log = new AuditLogResponse();
        log.setId(1L);
        log.setAction("UPDATE");
        log.setEntityType("SystemConfiguration");
        log.setEntityId("site.title");
        log.setOldValues("{\"value\":\"Old\"}");
        log.setNewValues("{\"value\":\"New\"}");
        // Campos opcionales no presentes en la entidad actual
        log.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));

        PageResponse<AuditLogResponse> page = new PageResponse<>(List.of(log), 0, 10, 1);

        when(adminAuditLogsService.list(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/admin/audit-logs?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].action").value("UPDATE"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.total").value(1));
    }
}

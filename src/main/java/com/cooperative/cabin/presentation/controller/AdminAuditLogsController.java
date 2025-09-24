package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.presentation.dto.AuditLogResponse;
import com.cooperative.cabin.presentation.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
@Validated
@Tag(name = "Admin Audit Logs", description = "Consulta de auditoría (admin)")
public class AdminAuditLogsController {

    private final AdminAuditLogsService adminAuditLogsService;

    public AdminAuditLogsController(AdminAuditLogsService adminAuditLogsService) {
        this.adminAuditLogsService = adminAuditLogsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listado paginado de auditorías", responses = @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class), examples = @ExampleObject(value = "{\n  \"items\": [\n    {\n      \"id\": 1,\n      \"userId\": \"admin\",\n      \"action\": \"UPDATE\",\n      \"entityType\": \"SystemConfiguration\",\n      \"entityId\": \"site.title\",\n      \"oldValues\": \"{\\\"value\\\":\\\"Old\\\"}\",\n      \"newValues\": \"{\\\"value\\\":\\\"New\\\"}\",\n      \"createdAt\": \"2024-01-01T00:00:00Z\"\n    }\n  ],\n  \"page\": 0,\n  \"size\": 10,\n  \"total\": 1\n}"))))
    public ResponseEntity<PageResponse<AuditLogResponse>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(adminAuditLogsService.list(page, size));
    }
}

package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.presentation.dto.AdminMetricsResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics")
@Validated
@Tag(name = "Admin Metrics", description = "Métricas de negocio y sistema (admin)")
public class AdminMetricsController {

    private final AdminMetricsService adminMetricsService;

    public AdminMetricsController(AdminMetricsService adminMetricsService) {
        this.adminMetricsService = adminMetricsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resumen de métricas", responses = @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminMetricsResponse.class), examples = @ExampleObject(value = "{\n  \"reservationsCreated\": 12,\n  \"reservationsCancelled\": 3,\n  \"statusTransitions\": 15,\n  \"schedulerTransitions\": 6\n}"))))
    public ResponseEntity<AdminMetricsResponse> getMetrics() {
        return ResponseEntity.ok(adminMetricsService.getSummary());
    }
}

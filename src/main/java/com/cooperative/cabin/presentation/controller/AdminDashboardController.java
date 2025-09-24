package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AdminDashboardService;
import com.cooperative.cabin.presentation.dto.AdminDashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Dashboard Admin", description = "Resumen general del sistema para administración")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Resumen del sistema", description = "Devuelve contadores clave y ocupación actual")
    @ApiResponse(responseCode = "200", description = "Resumen obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDashboardResponse.class), examples = @ExampleObject(value = """
            {
              "reservationsTotal": 124,
              "reservationsPending": 8,
              "reservationsConfirmed": 32,
              "reservationsInUse": 5,
              "reservationsCompleted": 70,
              "reservationsCancelled": 9,
              "activeUsers": 340,
              "activeCabins": 5,
              "occupancyRatePercent": 65
            }
            """)))
    public ResponseEntity<AdminDashboardResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}

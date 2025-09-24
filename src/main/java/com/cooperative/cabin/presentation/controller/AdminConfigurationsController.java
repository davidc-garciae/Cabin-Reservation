package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/configurations")
@Tag(name = "Admin Configurations", description = "Gestión de configuraciones del sistema")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class AdminConfigurationsController {

    private final ConfigurationService configurationService;

    public AdminConfigurationsController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las configuraciones", description = "Obtiene todas las configuraciones del sistema", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de configuraciones obtenida exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "max_reservations_per_user": "5",
                      "default_booking_days": "30",
                      "cancellation_hours": "24",
                      "maintenance_mode": "false",
                      "email_notifications": "true"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/configurations"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/configurations"
                    }
                    """)))
    })
    public ResponseEntity<Map<String, String>> list() {
        return ResponseEntity.ok(configurationService.getAll());
    }

    @Schema(description = "Solicitud de actualización de configuración")
    public static record UpdateConfigurationRequest(
            @Schema(description = "Nuevo valor de la configuración", example = "10") String value) {
    }

    @PutMapping("/{key}")
    @Operation(summary = "Actualizar configuración específica", description = "Actualiza el valor de una configuración específica del sistema", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevo valor de la configuración", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateConfigurationRequest.class), examples = @ExampleObject(value = """
            {
              "value": "10"
            }
            """))), responses = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Value is required",
                      "path": "/api/admin/configurations/max_reservations_per_user"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/configurations/max_reservations_per_user"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Configuration not found with key: invalid_key",
                      "path": "/api/admin/configurations/invalid_key"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/configurations/max_reservations_per_user"
                    }
                    """)))
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "Clave de la configuración", example = "max_reservations_per_user") @PathVariable("key") String key,
            @RequestBody UpdateConfigurationRequest request) {
        configurationService.setValue(key, request.value());
        return ResponseEntity.ok().build();
    }
}

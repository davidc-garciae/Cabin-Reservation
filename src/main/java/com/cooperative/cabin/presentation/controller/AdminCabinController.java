package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.CabinApplicationService;
import com.cooperative.cabin.presentation.dto.CabinResponse;
import com.cooperative.cabin.presentation.dto.CreateCabinRequest;
import com.cooperative.cabin.presentation.dto.UpdateCabinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cabins")
@Tag(name = "Administración de Cabañas", description = "Gestión completa de cabañas para administradores")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCabinController {

    private final CabinApplicationService cabinApplicationService;

    public AdminCabinController(CabinApplicationService cabinApplicationService) {
        this.cabinApplicationService = cabinApplicationService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las cabañas (Admin)", description = "Obtiene todas las cabañas del sistema, incluyendo las inactivas")
    @ApiResponse(responseCode = "200", description = "Lista de todas las cabañas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
            [
              { "id": 1, "name": "Cabaña Activa", "active": true },
              { "id": 2, "name": "Cabaña Inactiva", "active": false }
            ]
            """)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<List<CabinResponse>> getAllCabins() {
        List<CabinResponse> cabins = cabinApplicationService.getAllCabinsForAdmin();
        return ResponseEntity.ok(cabins);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cabaña por ID (Admin)", description = "Obtiene los detalles de una cabaña específica, incluyendo las inactivas")
    @ApiResponse(responseCode = "200", description = "Cabaña encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
            { "id": 1, "name": "Cabaña del Lago", "active": true }
            """)))
    @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            { "timestamp":"2024-01-01T10:00:00Z", "status":404, "error":"Not Found", "message":"Cabin not found" }
            """)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<CabinResponse> getCabinById(
            @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long id) {
        CabinResponse cabin = cabinApplicationService.getCabinByIdForAdmin(id);
        return ResponseEntity.ok(cabin);
    }

    @PostMapping
    @Operation(summary = "Crear nueva cabaña", description = "Crea una nueva cabaña en el sistema")
    @ApiResponse(responseCode = "201", description = "Cabaña creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
            { "id": 3, "name": "Nueva Cabaña", "capacity": 4, "active": true }
            """)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            { "timestamp":"2024-01-01T10:00:00Z", "status":400, "error":"Bad Request", "message":"El nombre es obligatorio" }
            """)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<CabinResponse> createCabin(
            @Valid @RequestBody CreateCabinRequest request) {
        CabinResponse cabin = cabinApplicationService.createCabin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cabin);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cabaña completa", description = "Actualiza todos los campos de una cabaña existente")
    @ApiResponse(responseCode = "200", description = "Cabaña actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinResponse.class), examples = @ExampleObject(value = """
            { "id": 1, "name": "Cabaña Actualizada", "capacity": 8, "active": true }
            """)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            { "timestamp":"2024-01-01T10:00:00Z", "status":400, "error":"Bad Request", "message":"La capacidad debe ser al menos 1" }
            """)))
    @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            { "timestamp":"2024-01-01T10:00:00Z", "status":404, "error":"Not Found", "message":"Cabin not found with id: 999" }
            """)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<CabinResponse> updateCabin(
            @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateCabinRequest request) {
        CabinResponse cabin = cabinApplicationService.updateCabin(id, request);
        return ResponseEntity.ok(cabin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cabaña", description = "Elimina permanentemente una cabaña del sistema")
    @ApiResponse(responseCode = "204", description = "Cabaña eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            { "timestamp":"2024-01-01T10:00:00Z", "status":404, "error":"Not Found", "message":"Cabin not found with id: 999" }
            """)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<Void> deleteCabin(
            @Parameter(description = "ID de la cabaña", example = "1") @PathVariable Long id) {
        cabinApplicationService.deleteCabin(id);
        return ResponseEntity.noContent().build();
    }
}
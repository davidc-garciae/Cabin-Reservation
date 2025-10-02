package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.AvailabilityBlocksAdminService;
import com.cooperative.cabin.domain.model.AvailabilityBlock;
import com.cooperative.cabin.presentation.dto.BlockResponse;
import com.cooperative.cabin.presentation.mapper.AvailabilityBlockMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/availability/blocks")
@Tag(name = "Admin Availability Blocks", description = "Bloqueos de disponibilidad (admin)")
@SecurityRequirement(name = "bearerAuth")
public class AdminAvailabilityBlocksController {

  private final AvailabilityBlocksAdminService service;
  private final AvailabilityBlockMapper mapper;

  public AdminAvailabilityBlocksController(AvailabilityBlocksAdminService service, AvailabilityBlockMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping
  @Operation(summary = "Listar todos los bloqueos de disponibilidad", description = "Obtiene todos los bloqueos de disponibilidad configurados en el sistema", responses = {
      @ApiResponse(responseCode = "200", description = "Lista de bloqueos obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BlockResponse.class)), examples = @ExampleObject(value = """
          [
            {
              "id": 1,
              "cabinId": 1,
              "startDate": "2024-02-15",
              "endDate": "2024-02-20",
              "reason": "Maintenance"
            },
            {
              "id": 2,
              "cabinId": 2,
              "startDate": "2024-03-01",
              "endDate": "2024-03-05",
              "reason": "Private event"
            }
          ]
          """))),
      @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Access Denied",
            "path": "/api/admin/availability/blocks"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/admin/availability/blocks"
          }
          """)))
  })
  public ResponseEntity<List<BlockResponse>> list() {
    List<AvailabilityBlock> list = service.list();
    List<BlockResponse> dto = list.stream().map(mapper::toResponse).toList();
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  @Operation(summary = "Crear bloqueo de disponibilidad", description = "Crea un nuevo bloqueo de disponibilidad para una cabaña en fechas específicas", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del bloqueo a crear", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlockRequest.class), examples = @ExampleObject(value = """
      {
        "cabinId": 1,
        "startDate": "2024-02-15",
        "endDate": "2024-02-20"
      }
      """))), responses = {
      @ApiResponse(responseCode = "201", description = "Bloqueo creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlockResponse.class), examples = @ExampleObject(value = """
          {
            "id": 1,
            "cabinId": 1,
            "startDate": "2024-02-15",
            "endDate": "2024-02-20",
            "reason": "Maintenance"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Invalid date range",
            "path": "/api/admin/availability/blocks"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Access Denied",
            "path": "/api/admin/availability/blocks"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Cabin not found with id: 1",
            "path": "/api/admin/availability/blocks"
          }
          """))),
      @ApiResponse(responseCode = "409", description = "Conflicto - Fechas ya bloqueadas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 409,
            "error": "Conflict",
            "message": "Dates already blocked for this cabin",
            "path": "/api/admin/availability/blocks"
          }
          """)))
  })
  public ResponseEntity<BlockResponse> create(@RequestBody BlockRequest request) {
    AvailabilityBlock created = service.create(request.getCabinId(), request.getStartDate(), request.getEndDate());
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
        .body(mapper.toResponse(created));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar bloqueo", responses = @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BlockResponse.class))))
  public ResponseEntity<BlockResponse> update(@PathVariable("id") Long id, @RequestBody BlockRequest request) {
    AvailabilityBlock updated = service.update(id, request.getCabinId(), request.getStartDate(),
        request.getEndDate());
    return ResponseEntity.ok(mapper.toResponse(updated));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar bloqueo de disponibilidad", description = "Elimina un bloqueo de disponibilidad específico por su ID", responses = {
      @ApiResponse(responseCode = "204", description = "Bloqueo eliminado exitosamente"),
      @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Access Denied",
            "path": "/api/admin/availability/blocks/1"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Bloqueo no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Availability block not found with id: 1",
            "path": "/api/admin/availability/blocks/1"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "timestamp": "2024-01-01T10:00:00.000+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "An unexpected error occurred",
            "path": "/api/admin/availability/blocks/1"
          }
          """)))
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID del bloqueo a eliminar", example = "1") @PathVariable("id") Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Schema(description = "Solicitud de bloqueo de disponibilidad")
  public static class BlockRequest {
    @Schema(description = "ID de la cabaña", example = "1")
    private Long cabinId;

    @Schema(description = "Fecha de inicio del bloqueo", example = "2024-02-15")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del bloqueo", example = "2024-02-20")
    private LocalDate endDate;

    public Long getCabinId() {
      return cabinId;
    }

    public void setCabinId(Long cabinId) {
      this.cabinId = cabinId;
    }

    public LocalDate getStartDate() {
      return startDate;
    }

    public void setStartDate(LocalDate startDate) {
      this.startDate = startDate;
    }

    public LocalDate getEndDate() {
      return endDate;
    }

    public void setEndDate(LocalDate endDate) {
      this.endDate = endDate;
    }
  }
}

package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.presentation.dto.ReservationResponse;
import com.cooperative.cabin.presentation.dto.PageResponse;
import com.cooperative.cabin.presentation.mapper.ReservationMapper;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
@Tag(name = "Admin Reservations", description = "Gestión de reservas (admin)")
@SecurityRequirement(name = "bearerAuth")
public class AdminReservationsController {

    private final ReservationApplicationService service;

    public AdminReservationsController(ReservationApplicationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Obtiene todas las reservas del sistema con paginación para administradores", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class), examples = @ExampleObject(value = """
                    {
                      "items": [
                        {
                          "id": 1,
                          "userId": 1,
                          "cabinId": 1,
                          "startDate": "2024-02-01",
                          "endDate": "2024-02-05",
                          "guests": 4,
                          "status": "CONFIRMED",
                          "totalPrice": 600.00,
                          "createdAt": "2024-01-01T10:00:00Z",
                          "updatedAt": "2024-01-01T10:00:00Z"
                        },
                        {
                          "id": 2,
                          "userId": 2,
                          "cabinId": 2,
                          "startDate": "2024-03-01",
                          "endDate": "2024-03-03",
                          "guests": 2,
                          "status": "PENDING",
                          "totalPrice": 300.00,
                          "createdAt": "2024-01-01T11:00:00Z",
                          "updatedAt": "2024-01-01T11:00:00Z"
                        }
                      ],
                      "page": 0,
                      "size": 20,
                      "total": 2,
                      "totalPages": 1
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/reservations"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/reservations"
                    }
                    """)))
    })
    public ResponseEntity<PageResponse<ReservationResponse>> listAll(
            @Parameter(description = "Número de página (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {
        List<Reservation> list = service.listAllForAdmin();
        List<ReservationResponse> dto = list.stream().map(ReservationMapper.INSTANCE::toResponse).toList();
        long total = dto.size();
        int from = Math.min(page * size, dto.size());
        int to = Math.min(from + size, dto.size());
        List<ReservationResponse> slice = dto.subList(from, to);
        return ResponseEntity.ok(new PageResponse<>(slice, page, size, total));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva del sistema por su ID (solo administradores)", responses = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Reservation not found with id: 1",
                      "path": "/api/admin/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar - Estado inválido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cannot delete reservation in current status",
                      "path": "/api/admin/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/reservations/1"
                    }
                    """)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la reserva a eliminar", example = "1") @PathVariable("id") Long id) {
        service.deleteByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}

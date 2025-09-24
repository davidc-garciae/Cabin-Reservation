package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.domain.model.ReservationStatus;
import com.cooperative.cabin.presentation.dto.ChangeReservationStatusRequest;
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

@RestController
@RequestMapping("/api/admin/reservations")
@Tag(name = "Admin Reservation (status)", description = "Cambio de estado de reservas por admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminReservationController {

    private final ReservationApplicationService reservationApplicationService;

    public AdminReservationController(ReservationApplicationService reservationApplicationService) {
        this.reservationApplicationService = reservationApplicationService;
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de la reserva", description = "Cambia el estado de una reserva específica. Transiciones válidas: PENDING→CONFIRMED/CANCELLED, CONFIRMED→CANCELLED, IN_USE→COMPLETED/CANCELLED", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevo estado de la reserva", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeReservationStatusRequest.class), examples = @ExampleObject(value = """
            {
              "status": "CONFIRMED"
            }
            """))), responses = {
            @ApiResponse(responseCode = "200", description = "Estado de reserva actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class), examples = @ExampleObject(value = """
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
                      "updatedAt": "2024-01-01T11:00:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Invalid status transition",
                      "path": "/api/admin/reservations/1/status"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Access Denied",
                      "path": "/api/admin/reservations/1/status"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Reservation not found with id: 1",
                      "path": "/api/admin/reservations/1/status"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "Transición de estado no válida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cannot transition from COMPLETED to CONFIRMED",
                      "path": "/api/admin/reservations/1/status"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/admin/reservations/1/status"
                    }
                    """)))
    })
    public ResponseEntity<Reservation> changeStatus(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable("id") Long reservationId,
            @RequestBody ChangeReservationStatusRequest request) {
        ReservationStatus to = ReservationStatus.valueOf(request.getStatus());
        Reservation updated = reservationApplicationService.changeStatusByAdmin(reservationId, to);
        return ResponseEntity.ok(updated);
    }
}

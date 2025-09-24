package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.presentation.dto.CreateReservationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Gestión de reservas de usuarios")
public class ReservationController {

    private final ReservationApplicationService reservationApplicationService;

    public ReservationController(ReservationApplicationService reservationApplicationService) {
        this.reservationApplicationService = reservationApplicationService;
    }

    @PostMapping
    @Operation(summary = "Crear pre-reserva", description = "Crea una nueva pre-reserva para una cabaña en fechas específicas", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva reserva", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateReservationRequest.class), examples = @ExampleObject(value = """
            {
              "userId": 1,
              "cabinId": 1,
              "startDate": "2024-02-01",
              "endDate": "2024-02-05",
              "guests": 4
            }
            """))), responses = {
            @ApiResponse(responseCode = "201", description = "Pre-reserva creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.cooperative.cabin.presentation.dto.ReservationResponse.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "cabinId": 1,
                      "startDate": "2024-02-01",
                      "endDate": "2024-02-05",
                      "guests": 4,
                      "status": "PENDING",
                      "totalPrice": 600.00,
                      "createdAt": "2024-01-01T10:00:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Invalid date range or guest count",
                      "path": "/api/reservations"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Cabaña no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Cabin not found with id: 1",
                      "path": "/api/reservations"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "Conflicto - Fechas no disponibles", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cabin not available for the selected dates",
                      "path": "/api/reservations"
                    }
                    """)))
    })
    public ResponseEntity<Reservation> create(@RequestBody CreateReservationRequest request) {
        Reservation created = reservationApplicationService.createPreReservation(
                request.getUserId(), request.getCabinId(), LocalDate.parse(request.getStartDate()),
                LocalDate.parse(request.getEndDate()),
                request.getGuests());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar reserva del usuario", description = "Cancela una reserva específica del usuario autenticado", responses = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.cooperative.cabin.presentation.dto.ReservationResponse.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "cabinId": 1,
                      "startDate": "2024-02-01",
                      "endDate": "2024-02-05",
                      "guests": 4,
                      "status": "CANCELLED",
                      "totalPrice": 600.00,
                      "createdAt": "2024-01-01T10:00:00Z",
                      "updatedAt": "2024-01-01T11:00:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Reservation not found with id: 1",
                      "path": "/api/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "No autorizado para cancelar esta reserva", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "You can only cancel your own reservations",
                      "path": "/api/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "No se puede cancelar - Estado inválido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cannot cancel reservation in current status",
                      "path": "/api/reservations/1"
                    }
                    """)))
    })
    public ResponseEntity<Reservation> cancel(
            @Parameter(description = "ID de la reserva a cancelar", example = "1") @PathVariable("id") Long reservationId,
            @Parameter(description = "ID del usuario autenticado", example = "1") @RequestHeader("X-User-Id") Long userId) {
        Reservation cancelled = reservationApplicationService.cancelByUser(userId, reservationId);
        return ResponseEntity.ok(cancelled);
    }
}

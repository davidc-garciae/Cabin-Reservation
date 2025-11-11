package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.ReservationApplicationService;
import com.cooperative.cabin.domain.model.Reservation;
import com.cooperative.cabin.presentation.dto.ReservationResponse;
import com.cooperative.cabin.presentation.mapper.ReservationMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Consulta de reservas del usuario")
@SecurityRequirement(name = "bearerAuth")
public class ReservationQueryController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationQueryController.class);
    private final ReservationApplicationService reservationApplicationService;

    public ReservationQueryController(ReservationApplicationService reservationApplicationService) {
        this.reservationApplicationService = reservationApplicationService;
    }

    @GetMapping
    @Operation(summary = "Listar reservas del usuario", description = "Obtiene todas las reservas del usuario autenticado", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class)), examples = @ExampleObject(value = """
                    [
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
                        "userId": 1,
                        "cabinId": 2,
                        "startDate": "2024-03-01",
                        "endDate": "2024-03-03",
                        "guests": 2,
                        "status": "PENDING",
                        "totalPrice": 300.00,
                        "createdAt": "2024-01-01T11:00:00Z",
                        "updatedAt": "2024-01-01T11:00:00Z"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Full authentication is required to access this resource",
                      "path": "/api/reservations"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/reservations"
                    }
                    """)))
    })
    public ResponseEntity<List<ReservationResponse>> list(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        logger.info("ReservationQueryController.list() - Received userId: {}", userId);
        try {
            if (userId == null) {
                logger.error("ReservationQueryController.list() - userId is null!");
                throw new IllegalStateException("User ID is required but was not found in request");
            }
            List<Reservation> list = reservationApplicationService.listByUser(userId);
            logger.info("ReservationQueryController.list() - Found {} reservations for userId: {}", list.size(), userId);
            List<ReservationResponse> dto = list.stream().map(ReservationMapper.INSTANCE::toResponse).toList();
            logger.info("ReservationQueryController.list() - Successfully mapped {} reservations to DTOs", dto.size());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("ReservationQueryController.list() - Error processing request for userId: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de reserva", description = "Obtiene los detalles de una reserva específica del usuario autenticado", responses = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class), examples = @ExampleObject(value = """
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
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Full authentication is required to access this resource",
                      "path": "/api/reservations/1"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "No autorizado para ver esta reserva", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "You can only view your own reservations",
                      "path": "/api/reservations/1"
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
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-01-01T10:00:00.000+00:00",
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "An unexpected error occurred",
                      "path": "/api/reservations/1"
                    }
                    """)))
    })
    public ResponseEntity<ReservationResponse> get(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable("id") Long id) {
        logger.info("ReservationQueryController.get() - Received userId: {}, reservationId: {}", userId, id);
        try {
            if (userId == null) {
                logger.error("ReservationQueryController.get() - userId is null!");
                throw new IllegalStateException("User ID is required but was not found in request");
            }
            Reservation r = reservationApplicationService.getByIdForUser(userId, id);
            logger.info("ReservationQueryController.get() - Found reservation {} for userId: {}", id, userId);
            ReservationResponse response = ReservationMapper.INSTANCE.toResponse(r);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("ReservationQueryController.get() - Error processing request for userId: {}, reservationId: {}", 
                    userId, id, e);
            throw e;
        }
    }
}

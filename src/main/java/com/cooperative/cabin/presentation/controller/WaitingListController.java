package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.WaitingListApplicationService;
import com.cooperative.cabin.presentation.dto.waitinglist.ClaimRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/waiting-list")
@Tag(name = "Waiting List")
public class WaitingListController {

    private final WaitingListApplicationService service;

    public WaitingListController(WaitingListApplicationService service) {
        this.service = service;
    }

    @PostMapping("/claim")
    @Operation(summary = "Reclama prioridad usando el token de notificación", description = "Requiere autenticación. Valida token y ventana; crea la reserva si hay disponibilidad.", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Claim exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WaitingListApplicationService.ClaimResult.class), examples = @ExampleObject(value = "{\n  \"reservationId\": null,\n  \"waitingListId\": 10,\n  \"userId\": 5,\n  \"cabinId\": 1\n}"))),
            @ApiResponse(responseCode = "410", description = "Token expirado o no válido", content = @Content),
            @ApiResponse(responseCode = "409", description = "Rango sin disponibilidad", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    })
    public ResponseEntity<?> claim(@RequestBody ClaimRequest req) {
        var res = service
                .claim(new WaitingListApplicationService.ClaimCommand(req.notifyToken(), req.numberOfGuests()));
        return res.<ResponseEntity<?>>map(r -> ResponseEntity.ok(r))
                .orElseGet(() -> ResponseEntity.status(410).build());
    }
}

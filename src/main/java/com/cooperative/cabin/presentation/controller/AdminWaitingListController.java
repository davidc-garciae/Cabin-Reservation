package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.application.service.WaitingListApplicationService;
import com.cooperative.cabin.presentation.dto.waitinglist.NotifyNextRequest;
import com.cooperative.cabin.presentation.dto.waitinglist.NotifyNextResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/admin/waiting-list")
@Tag(name = "Waiting List - Admin", description = "Operaciones administrativas sobre la lista de espera")
public class AdminWaitingListController {

    private final WaitingListApplicationService service;

    public AdminWaitingListController(WaitingListApplicationService service) {
        this.service = service;
    }

    @PostMapping("/notify-next")
    @Operation(summary = "Notifica al siguiente en la lista de espera para un hueco liberado", description = "Requiere rol ADMIN. Genera un token de notificación y una ventana de oportunidad para claim.", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidato notificado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotifyNextResponse.class), examples = @ExampleObject(value = "{\n  \"waitingListId\": 10,\n  \"userId\": 5,\n  \"cabinId\": 1,\n  \"notifyToken\": \"b4b7a3d5-1a3c-4a2b-bb89-2ca6b0f3e6fa\",\n  \"notifyExpiresAt\": \"2025-03-09T12:00:00\"\n}"))),
            @ApiResponse(responseCode = "204", description = "No hay pendientes para notificar"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Prohibido (solo ADMIN)", content = @Content)
    })
    public ResponseEntity<?> notifyNext(
            @Parameter(description = "Datos del hueco liberado", required = true) @RequestBody NotifyNextRequest req) {
        var res = service.notifyNext(new WaitingListApplicationService.NotifyNextCommand(
                req.cabinId(), req.startDate(), req.endDate(),
                req.windowHours() != null ? req.windowHours() : 4));
        return res.<ResponseEntity<?>>map(r -> ResponseEntity.ok(new NotifyNextResponse(
                r.waitingListId(), r.userId(), r.cabinId(), r.notifyToken(), r.notifyExpiresAt())))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
